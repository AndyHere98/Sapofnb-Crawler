package com.andy.sapofnbcrawler.service;

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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final String URI    = SapoUtils.URI;
    private final String COOKIE = SapoUtils.COOKIE;
    
    private final MenuService            menuService;
    private final IOrderRepository       orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    
    
    public Object summaryTodayOrder() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        List<Order> orders = orderRepository.getOrderByOrderDate(sdf.format(today));
        if (orders.isEmpty()) { return "Hôm nay không có đơn nào được đặt!";}
        Map<String, Integer> summaryDishes = new HashMap<>();
        Map<String, BigDecimal> summaryPrice = new HashMap<>();
        for (Order order : orders) {
            orderDetailRepository.findAllByOrder(order).forEach(orderDetail -> {
                summaryDishes.put(orderDetail.getDishName(), summaryDishes.get(orderDetail.getDishName()) == null ? orderDetail.getQuantity(): summaryDishes.get(orderDetail.getDishName()) + orderDetail.getQuantity());
                summaryPrice.put(orderDetail.getDishName(), orderDetail.getPrice().multiply(BigDecimal.valueOf(summaryDishes.get(orderDetail.getDishName()))));
                System.out.println("-------------------Start----------------");
                System.out.println("Món: " + orderDetail.getDishName());
                System.out.println("Số lương : " + summaryDishes.get(orderDetail.getDishName()));
                System.out.println("Giá: " + orderDetail.getPrice());
                System.out.println("-------------------End----------------");
            });
        }
        System.out.println(summaryDishes);
        System.out.println(summaryPrice);
        OrderResponse                    orderResponse = new OrderResponse();
        List<OrderResponse.DishResponse> dishes        = new ArrayList<>();
        
        for (Map.Entry<String, Integer> dishItem : summaryDishes.entrySet()) {
            OrderResponse.DishResponse dish = new OrderResponse().new DishResponse();
            dish.setDishName(dishItem.getKey());
            dish.setQuantity(dishItem.getValue());
            dish.setMoney(summaryPrice.get(dishItem.getKey()));
            dishes.add(dish);
        }
        orderResponse.setDishes(dishes);
        orderResponse.setOrderDate(sdf.format(today));
        orderResponse.setTotalPrice(BigDecimal.valueOf(summaryPrice.values().stream().mapToDouble(BigDecimal::doubleValue).sum()));
        
        return orderResponse;
    }
    
    public Object summaryTodayOrderByMember () {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        List<Order> orders = orderRepository.getOrdersByOrderDateOrderByCustomerName(sdf.format(today));
        if (orders.isEmpty()) { return "Hôm nay không có đơn nào được đặt!";}
        
        List<OrderResponse> orderResponseList   = new ArrayList<>();
        
        for (Order order : orders) {
            OrderResponse orderResponse = new OrderResponse();
            List<OrderResponse.DishResponse> dishResponseList = new ArrayList<>();
            
            BigDecimal totalPrice = BigDecimal.ZERO;
            List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);
            for (OrderDetail orderDetail : orderDetailList) {
                OrderResponse.DishResponse dishResponse = new OrderResponse().new DishResponse();
                BeanUtils.copyProperties(orderDetail, dishResponse);
                dishResponse.setMoney(orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
                totalPrice = totalPrice.add(dishResponse.getMoney());
                dishResponseList.add(dishResponse);
            }
            OrderResponse.CustomerInfo customerInfo = new OrderResponse().new CustomerInfo();
            customerInfo.setCustomerName(order.getCustomerName());
            customerInfo.setPhone(order.getCustomerPhone());
            orderResponse.setCustomerInfo(customerInfo);
            orderResponse.setTotalPrice(totalPrice);
            orderResponse.setDishes(dishResponseList);
            orderResponse.setPaymentMethodType(order.getPaymentMethodType());
            orderResponse.setPaymentMethodName(SapoUtils.getPayment(order.getPaymentMethodType()));
            orderResponse.setOrderDate(sdf.format(today));
            orderResponseList.add(orderResponse);
        }
        
        return orderResponseList;
        
    }
    
}
