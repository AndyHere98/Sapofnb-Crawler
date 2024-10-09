package com.andy.sapofnbcrawler.service;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.common.SapoUtils;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.entity.OrderDetail;
import com.andy.sapofnbcrawler.repository.IOrderDetailRepository;
import com.andy.sapofnbcrawler.repository.IOrderRepository;
import com.andy.sapofnbcrawler.request.MemberOrderRequest;
import com.andy.sapofnbcrawler.request.MemberOrderRequest.DishRequest;
import com.andy.sapofnbcrawler.request.OrderRequest;
import com.andy.sapofnbcrawler.response.MemberOrderResponse;
import com.andy.sapofnbcrawler.response.MenuResponse;
import com.andy.sapofnbcrawler.response.OrderResponse;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final String URI    = SapoConstants.URI;
    private final String COOKIE = SapoConstants.COOKIE;
    
    private final MenuService            menuService;
    private final IOrderRepository       orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    
    
    public Object getCartOrder() {
        
        RestTemplate restTemplate = new RestTemplate();
        
        StringBuilder sUrl = new StringBuilder();
        sUrl.append(URI);
        sUrl.append("/carts");
        
        String queryParam = "customer_online_name={cusName}&customer_online_phone={cusPhone}";
        
        Map<String, String> queryParamMap = new HashMap<>();
        queryParamMap.put("cusName", "Nguyên");
        queryParamMap.put("cusPhone", "0784202149");
        
        UriComponents urlBuilder = UriComponentsBuilder.fromUriString(sUrl.toString()).query(queryParam)
                                                       .buildAndExpand(queryParamMap);
        
        HttpHeaders httpHeaders = restTemplate.headForHeaders(urlBuilder.toString());
        httpHeaders.add("Cookie", COOKIE);
        
        HttpEntity<String> httpEntity = new HttpEntity<>("Andy", httpHeaders);
        
        ResponseEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET, httpEntity,
                                                                String.class);
        
        String json = SapoUtils.getJsonData(response.getBody());
        if (json.isEmpty())
            return null;
        
        OrderRequest  orderRequest  = (OrderRequest) SapoUtils.convertJsonToObject(json, OrderRequest.class);
        
        return mappingOrderResponse(orderRequest);
    }
    
    private OrderResponse mappingOrderResponse(OrderRequest orderRequest) {
        OrderResponse                    orderResponse = new OrderResponse();
        OrderResponse.DishResponse       dishResponse  = new OrderResponse().new DishResponse();
        List<OrderResponse.DishResponse> dishes        = new ArrayList<>();
        
        BeanUtils.copyProperties(orderRequest, orderResponse);
        orderResponse.setFullAddress(orderRequest.getShipment().getShipmentAddress().getFullAddress());
        
        for (int i = 0; i < orderRequest.getDishes().size(); i++) {
            dishResponse = new OrderResponse().new DishResponse();
            BeanUtils.copyProperties(orderRequest.getDishes().get(i), dishResponse);
            dishes.add(dishResponse);
        }
        
        orderResponse.setDishes(dishes);
        OrderResponse.CustomerInfo cusResponse = new OrderResponse().new CustomerInfo();
        BeanUtils.copyProperties(orderRequest.getCustomerInfo(), cusResponse);
        
        orderResponse.setCustomerInfo(cusResponse);
        return orderResponse;
    }
    
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public Object placeOrder(MemberOrderRequest request) {
        // Checking member has been order today or not
        Optional<Order> orderCheck = orderRepository.getOrderByOrderDateOrderByCustomerName(request);
        if (orderCheck.isPresent()) {return request.getCustomerName() + " đã đặt đơn hôm nay, vui lòng chỉnh sửa hoặc huỷ đơn trước 9h30 AM.";}
        
        Order order = mappingToOrder(request);
        order.setId(UUID.randomUUID().toString());
        order.setCreatedDate(new Date());
        order.setOrderDate(new Date());
        List<OrderDetail> orderDetails = request.getDishes().stream().map(this::mappingToOrderDetail).collect(
                Collectors.toList());
        
        MenuResponse menu        = menuService.getMenu();
        List<String> todayDishes = menu.getDishes().stream().map(MenuResponse.DishResponse::getName).toList();
        
        try {
            if (orderDetails.isEmpty()) throw new RuntimeException("Danh sách món ăn đang trống");
            
            orderDetails.forEach(orderDetail -> {
                orderDetail.setOrder(order);
                if (!todayDishes.contains(orderDetail.getDishName())) throw new RuntimeException(
                        "Món " + orderDetail.getDishName() + " không nằm trong danh sách menu hôm nay: " + todayDishes.toString());
            });
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        
        order.setOrderDetails(orderDetails);
        
        return mappingOrderToMemberOrderResponse(order);
    }
    
    private MemberOrderResponse mappingOrderToMemberOrderResponse(Order order) {
        MemberOrderResponse orderResponse = new MemberOrderResponse();
        MemberOrderResponse.CustomerInfo cusResponse = new MemberOrderResponse.CustomerInfo();
        List<MemberOrderResponse.DishResponse> dishes = new ArrayList<>();
        
        orderResponse.setOrderSku(order.getId());
        orderResponse.setPaymentMethodType(order.getPaymentMethodType());
        orderResponse.setPaymentMethodName(SapoUtils.getPayment(order.getPaymentMethodType()));
        orderResponse.setCreatedOn(order.getCreatedDate());
        orderResponse.setModifiedOn(order.getUpdateDate());
        orderResponse.setTotalPrice(order.getTotalPrice());
        
        cusResponse.setCustomerName(order.getCustomerName());
        cusResponse.setCustomerPhone(order.getCustomerPhone());
        cusResponse.setCustomerEmail(order.getCustomerEmail());
        orderResponse.setCustomerInfo(cusResponse);
        
        dishes = order.getOrderDetails().stream().map(detail -> {
            MemberOrderResponse.DishResponse dish = new MemberOrderResponse.DishResponse();
            dish.setDishName(detail.getDishName());
            dish.setQuantity(detail.getQuantity());
            dish.setPrice(detail.getPrice());
            return dish;
        }).toList();
        orderResponse.setDishes(dishes);
        
        return orderResponse;
    }
    
    private Order mappingToOrder(MemberOrderRequest request) {
        Order order = new Order();
        BeanUtils.copyProperties(request, order);
        order.setUpdateDate(new Date());
        
        return order;
    }
    
    private OrderDetail mappingToOrderDetail(DishRequest request) {
        OrderDetail orderDetail = new OrderDetail();
        BeanUtils.copyProperties(request, orderDetail);
        orderDetail.setOrderDate(new Date());
        return orderDetail;
    }
    
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public Object editOrder(String id, MemberOrderRequest request) {
        
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date timeup = sdf.parse(sdf.format(currentDate).substring(0,10) + " 09:30:00");
            if (currentDate.after(timeup)) return "Không thể huỷ đơn sau 9h30 AM";
        } catch (ParseException e) {
            return sdf.format(currentDate).substring(0,10) + " 09:30:00";
        }
        
        Optional<Order> order = orderRepository.findById(id);
        try {
            if (order.isEmpty()) {throw new RuntimeException("Order số: " + id + " không tồn tại trong hệ thống, Vui lòng kiểm tra lại");}
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        
        List<OrderDetail> orderDetails = request.getDishes().stream().map(this::mappingToOrderDetail).collect(
                Collectors.toList());
        
        MenuResponse menu        = menuService.getMenu();
        List<String> todayDishes = menu.getDishes().stream().map(MenuResponse.DishResponse::getName).toList();
        
        try {
            if (orderDetails.isEmpty()) throw new RuntimeException("Danh sách món ăn đang trống");
            orderDetails.forEach(orderDetail -> {
                orderDetail.setOrder(order.get());
                if (!todayDishes.contains(orderDetail.getDishName())) throw new RuntimeException(
                        "Món " + orderDetail.getDishName() + " không nằm trong danh sách menu hôm nay: " + todayDishes.toString());
            });
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        
        orderDetailRepository.saveAll(orderDetails);
        orderRepository.save(order.get());
        
        order.get().setOrderDetails(orderDetails);
        
        return mappingOrderToMemberOrderResponse(order.get());
    }
    
    public Object getOrderById(String id) {
        Optional<Order> order = orderRepository.findById(id);
        try {
            if (order.isEmpty()) {throw new RuntimeException("Order số: " + id + " không tồn tại trong hệ thống, Vui lòng kiểm tra lại");}
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order.get());
        order.get().setOrderDetails(orderDetails);
        return mappingOrderToMemberOrderResponse(order.get());
    }
    
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public Object deleteOrder(String id) {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date timeup = sdf.parse(sdf.format(currentDate).substring(0,10) + " 09:30:00");
            if (currentDate.after(timeup)) return "Không thể huỷ đơn sau 9h30 AM";
        } catch (ParseException e) {
            return sdf.format(currentDate).substring(0,10) + " 09:30:00";
        }
        
        Optional<Order> order = orderRepository.findById(id);
        try {
            if (order.isEmpty()) {throw new RuntimeException("Order số: " + id + " không tồn tại trong hệ thống, Vui lòng kiểm tra lại");}
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        orderDetailRepository.deleteOrderDetailByOrder(order.get());
        orderRepository.delete(order.get());
        return "Bạn đã huỷ thành công đơn hàng";
    }
}
