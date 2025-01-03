package com.andy.sapofnbcrawler.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
import com.andy.sapofnbcrawler.validation.OrderValidation;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final String URI    = SapoConstants.URI;
    private final String COOKIE = SapoConstants.COOKIE;
    
    private final MenuService            menuService;
    private final IOrderRepository       orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    
    @Value("${sapo.customer.name}")
    private String customerName;
    @Value("${sapo.customer.phone}")
    private String customerPhone;

    @Value("${sapo-mode}")
    private static String mode;
	
    public Object getCartOrder() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        
        StringBuilder sUrl = new StringBuilder();
        sUrl.append(URI);
        sUrl.append("/carts");
        
        String queryParam = "customer_online_name={cusName}&customer_online_phone={cusPhone}";
        
        Map<String, String> queryParamMap = new HashMap<>();
        queryParamMap.put("cusName", customerName);
        queryParamMap.put("cusPhone", customerPhone);
        
        UriComponents urlBuilder = UriComponentsBuilder.fromUriString(sUrl.toString()).query(queryParam)
                                                       .buildAndExpand(queryParamMap);
        
        HttpHeaders httpHeaders = restTemplate.headForHeaders(urlBuilder.toString());
        httpHeaders.add("Cookie", COOKIE);
        
        HttpEntity<String> httpEntity = new HttpEntity<>("Andy", httpHeaders);
        
        ResponseEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET, httpEntity,
                                                                String.class);
        
        String json = SapoUtils.getJsonData(response.getBody());
        if (json.isEmpty())
			throw new Exception("There is no response received " + response.getBody());
        
        OrderRequest  orderRequest  = (OrderRequest) SapoUtils.convertJsonToObject(json, OrderRequest.class);
        
        return mappingOrderResponse(orderRequest);
    }
    
    private OrderResponse mappingOrderResponse(OrderRequest orderRequest) {
        OrderResponse                    orderResponse = new OrderResponse();
        OrderResponse.DishResponse       dishResponse  = new OrderResponse.DishResponse();
        List<OrderResponse.DishResponse> dishes        = new ArrayList<>();
        
        BeanUtils.copyProperties(orderRequest, orderResponse);
        orderResponse.setFullAddress(orderRequest.getShipment().getShipmentAddress().getFullAddress());
        
        for (int i = 0; i < orderRequest.getDishes().size(); i++) {
            dishResponse = new OrderResponse.DishResponse();
            BeanUtils.copyProperties(orderRequest.getDishes().get(i), dishResponse);
            dishes.add(dishResponse);
        }
        
        orderResponse.setDishes(dishes);
        OrderResponse.CustomerInfo cusResponse = new OrderResponse.CustomerInfo();
        BeanUtils.copyProperties(orderRequest.getCustomerInfo(), cusResponse);
        
        orderResponse.setCustomerInfo(cusResponse);
        return orderResponse;
    }
    
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public Object placeOrder(MemberOrderRequest request) throws Exception {
    	
    	if (SapoConstants.APP_MODE_PRODUCTION.equalsIgnoreCase(mode)) {
			Object rtnObject = SapoUtils.checkingTimeUp();
			if (!rtnObject.getClass().isInstance(Boolean.class)) {
				return rtnObject;
			}
    	}
    	
        // Checking member has been order today or not
        Optional<Order> orderCheck = orderRepository.getOrderByOrderDateOrderByCustomerName(request);
        OrderValidation.isOrderFinal(orderCheck, request.getCustomerName());
        
        Order order = mappingToOrder(request);
        order.setId(UUID.randomUUID().toString());
//        order.setCreatedBy(request.getCustomerName());
//        order.setCreatedDate(LocalDateTime.now());
        order.setOrderDate(LocalDateTime.now());
        List<OrderDetail> orderDetails = request.getDishes().stream().map(this::mappingToOrderDetail).collect(
                Collectors.toList());
        OrderValidation.isNoDishes(orderDetails); // Check no dish mapped
        
        MenuResponse menu = menuService.getMenu();
        List<String> todayDishes = menu.getDishes().stream().map(MenuResponse.DishResponse::getName).toList();
        
        
        orderDetails.forEach(orderDetail -> {
            orderDetail.setOrder(order);
            OrderValidation.checkDishIsNotInMenu(todayDishes, orderDetail);
        });
        
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
        order.setUpdateDate(LocalDateTime.now());
        
        return order;
    }
    
    private OrderDetail mappingToOrderDetail(DishRequest request) {
        OrderDetail orderDetail = new OrderDetail();
        BeanUtils.copyProperties(request, orderDetail);
        orderDetail.setOrderDate(LocalDateTime.now());
        return orderDetail;
    }
    
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public Object editOrder(String id, MemberOrderRequest request) throws Exception {
        
    	if (SapoConstants.APP_MODE_PRODUCTION.equalsIgnoreCase(mode)) {
			Object rtnObject = SapoUtils.checkingTimeUp();
			if (!rtnObject.getClass().isInstance(Boolean.class)) {
				return rtnObject;
			}
    	}
        
        Optional<Order> optionalOrder = orderRepository.findById(id);
        Order order = OrderValidation.isValidOrder(optionalOrder, id);
        
        List<OrderDetail> orderDetails = request.getDishes().stream().map(this::mappingToOrderDetail).collect(
                Collectors.toList());
        OrderValidation.isNoDishes(orderDetails); // Check no dish mapped
        
        MenuResponse menu        = menuService.getMenu();
        List<String> todayDishes = menu.getDishes().stream().map(MenuResponse.DishResponse::getName).toList();
        
        orderDetails.forEach(orderDetail -> {
            orderDetail.setOrder(order);
            OrderValidation.checkDishIsNotInMenu(todayDishes, orderDetail);
        });
        
        orderDetailRepository.saveAll(orderDetails);
        orderRepository.save(order);
        
        order.setOrderDetails(orderDetails);
        
        return mappingOrderToMemberOrderResponse(order);
    }
    
    public Object getOrderById(String id) throws Exception {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        Order order = OrderValidation.isValidOrder(optionalOrder, id);
        
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);
        order.setOrderDetails(orderDetails);
        return mappingOrderToMemberOrderResponse(order);
    }
    
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public Object deleteOrder(String id) throws Exception {

    	if (SapoConstants.APP_MODE_PRODUCTION.equalsIgnoreCase(mode)) {
			Object rtnObject = SapoUtils.checkingTimeUp();
			if (!rtnObject.getClass().isInstance(Boolean.class)) {
				return rtnObject;
			}
    	}
        
        Optional<Order> optionalOrder = orderRepository.findById(id);
        Order order = OrderValidation.isValidOrder(optionalOrder, id);
        orderDetailRepository.deleteOrderDetailByOrder(order);
        orderRepository.delete(order);
        
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderSku(order.getId());
        orderResponse.setTotalPrice(order.getTotalPrice());
        return orderResponse;
    }
    
}
