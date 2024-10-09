package com.andy.sapofnbcrawler.service;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.common.SapoUtils;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.entity.OrderDetail;
import com.andy.sapofnbcrawler.repository.IOrderDetailRepository;
import com.andy.sapofnbcrawler.repository.IOrderRepository;
import com.andy.sapofnbcrawler.request.SummaryRequest;
import com.andy.sapofnbcrawler.response.OrderResponse;
import hirondelle.date4j.DateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final IOrderRepository       orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    
    
    public Object summaryTodayOrder() {
        SimpleDateFormat sdf    = new SimpleDateFormat("dd/MM/yyyy");
        Date             today  = new Date();
        List<Order>      orders = orderRepository.getOrderByOrderDate(sdf.format(today));
        if (orders.isEmpty()) {
            return "Hôm nay không có đơn nào được đặt!";
        }
        Map<String, Integer>    summaryDishes = new HashMap<>();
        Map<String, BigDecimal> summaryPrice  = new HashMap<>();
        for (Order order : orders) {
            orderDetailRepository.findAllByOrder(order).forEach(orderDetail -> {
                summaryDishes.put(orderDetail.getDishName(),
                                  summaryDishes.get(orderDetail.getDishName()) == null ? orderDetail.getQuantity() :
                                  summaryDishes.get(orderDetail.getDishName()) +
                                  orderDetail.getQuantity());
                summaryPrice.put(orderDetail.getDishName(), orderDetail.getPrice()
                                                                       .multiply(BigDecimal.valueOf(summaryDishes.get(
                                                                               orderDetail.getDishName()))));
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
        orderResponse.setTotalPrice(
                BigDecimal.valueOf(summaryPrice.values().stream().mapToDouble(BigDecimal::doubleValue).sum()));
        
        return orderResponse;
    }
    
    public Object summaryTodayOrderByMember() {
        SimpleDateFormat sdf     = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date             today   = new Date();
        List<Order>      orders  = orderRepository.getOrdersByOrderDateOrderByCustomerName(sdf.format(today));
        if (orders.isEmpty()) {
            return "Hôm nay không có đơn nào được đặt!";
        }
        
        List<OrderResponse> orderResponseList = new ArrayList<>();
        
        for (Order order : orders) {
            OrderResponse                    orderResponse    = new OrderResponse();
            List<OrderResponse.DishResponse> dishResponseList = new ArrayList<>();
            
            BigDecimal        totalPrice      = BigDecimal.ZERO;
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
            orderResponse.setCreatedOn(sdfTime.format(order.getCreatedDate().getTime()));
            orderResponse.setModifiedOn(sdfTime.format(order.getUpdateDate().getTime()));
            orderResponseList.add(orderResponse);
        }
        
        return orderResponseList;
        
    }
    
    public Object summaryOrdersByTime(SummaryRequest request) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        
        DateTime today = DateTime.now(TimeZone.getDefault());
        DateTime diffDate = today;
        String day = "";
        
        List<Order> orderList = new ArrayList<>();
        
        try {
            
            switch (request.getUnit().toUpperCase()) {
                case SapoConstants.UNIT_DAY -> {
                    diffDate = today.minus(0, 0, request.getQuantity(), 0,
                                           0, 0, 0, null);
                    
                    cal.setTime(sdfInput.parse(diffDate.toString().substring(0,10)));
                    day = sdf.format(cal.getTime());
                }
                case SapoConstants.UNIT_WEEK -> {
                    diffDate = today.minus(0, 0, request.getQuantity()*7, 0,
                                           0, 0, 0, null);
                    cal.setTime(sdfInput.parse(diffDate.toString().substring(0,10)));
                    cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR));
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    day = sdf.format(cal.getTime());
                }
                case SapoConstants.UNIT_MONTH -> {
                    diffDate = today.minus(0, request.getQuantity(), 0, 0,
                                           0, 0, 0, null);
                    cal.setTime(sdfInput.parse(diffDate.getStartOfMonth().toString().substring(0,10)));
                    day = sdf.format(cal.getTime());
                }
                case SapoConstants.UNIT_YEAR -> {
                    day = "01/01/" + (today.getYear() - request.getQuantity());
                }
                default -> {
                    day = sdf.format(today.toString().substring(0,10));
                }
            }
            
            orderList = orderRepository.getOrderByOrderDateAndCustomerNameOrderByOrderDateAsc(
                    request.getCustomerName(), day);
            
            if (orderList.isEmpty()) {return "Không có đơn hàng nào được đặt";}
            
            for (Order order : orderList) {
                List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);
                order.setOrderDetails(orderDetailList);
            }
            // TODO: mapping order list to order response
        } catch (Exception e) {
            return e.getMessage();
        }
        System.out.println(diffDate.toString());
        return orderList;
    }
}
