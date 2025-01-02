package com.andy.sapofnbcrawler.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.common.SapoUtils;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.entity.OrderDetail;
import com.andy.sapofnbcrawler.exception.OrderNotFoundException;
import com.andy.sapofnbcrawler.repository.IOrderDetailRepository;
import com.andy.sapofnbcrawler.repository.IOrderRepository;
import com.andy.sapofnbcrawler.request.SummaryRequest;
import com.andy.sapofnbcrawler.response.OrderResponse;
import com.andy.sapofnbcrawler.response.SummaryResponse;

import hirondelle.date4j.DateTime;
import lombok.RequiredArgsConstructor;

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
            throw new OrderNotFoundException("Hôm nay không có đơn nào được đặt!");
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
            OrderResponse.DishResponse dish = new OrderResponse.DishResponse();
            dish.setDishName(dishItem.getKey());
            dish.setQuantity(dishItem.getValue());
            dish.setPrice(summaryPrice.get(dishItem.getKey()));
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
            throw new OrderNotFoundException("Hôm nay không có đơn nào được đặt!");
        }
        
        List<OrderResponse> orderResponseList = new ArrayList<>();
        
        for (Order order : orders) {
            OrderResponse                    orderResponse    = new OrderResponse();
            List<OrderResponse.DishResponse> dishResponseList = new ArrayList<>();
            
            BigDecimal        totalPrice      = BigDecimal.ZERO;
            List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);
            for (OrderDetail orderDetail : orderDetailList) {
                OrderResponse.DishResponse dishResponse = new OrderResponse.DishResponse();
                BeanUtils.copyProperties(orderDetail, dishResponse);
                dishResponse.setPrice(orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
                totalPrice = totalPrice.add(dishResponse.getPrice());
                dishResponseList.add(dishResponse);
            }
            OrderResponse.CustomerInfo customerInfo = new OrderResponse.CustomerInfo();
            customerInfo.setCustomerName(order.getCustomerName());
            customerInfo.setPhone(order.getCustomerPhone());
            orderResponse.setCustomerInfo(customerInfo);
            orderResponse.setTotalPrice(totalPrice);
            orderResponse.setDishes(dishResponseList);
            orderResponse.setPaymentMethodType(order.getPaymentMethodType());
            orderResponse.setPaymentMethodName(SapoUtils.getPayment(order.getPaymentMethodType()));
            orderResponse.setOrderDate(sdf.format(today));
            orderResponse.setCreatedOn(sdfTime.format(order.getCreatedDate()));
            orderResponse.setModifiedOn(sdfTime.format(order.getUpdateDate()));
            orderResponseList.add(orderResponse);
        }
        
        return orderResponseList;
        
    }
    
    public Object summaryOrdersByTime(SummaryRequest request) throws ParseException {
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        
        DateTime today = DateTime.now(TimeZone.getDefault());
        DateTime diffDate = today;
        String day = "";
        
        List<Order> orderList = new ArrayList<>();
        List<OrderResponse> orderResponses = new ArrayList<>();
        List<SummaryResponse> rtnList = new ArrayList<>();
        
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
            
            if (orderList.isEmpty()) {
            	throw new OrderNotFoundException("Hôm nay không có đơn nào được đặt!");
            }
            
            Map<String, List<Order>> summaryMap = new HashMap<>();
            List<Order> orderMappingList = new ArrayList<>();
            
            for (Order order : orderList) {
            	String orderDate = sdf.format(order.getOrderDate());

                List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);
                order.setOrderDetails(orderDetailList);
                
            	if (summaryMap.get(orderDate) == null) {
            		orderMappingList = new ArrayList<>();
            		orderMappingList.add(order);
            	} else {
            		orderMappingList.add(order);
            	}
            	summaryMap.put(orderDate, orderMappingList);
            }
            for (Map.Entry<String, List<Order>> map : summaryMap.entrySet()) {
            	SummaryResponse response = new SummaryResponse();
            	response.setOrderDate(day);
            	List<Order> orders = map.getValue();
            	response.setTotalPrice(BigDecimal.valueOf(orders.stream().mapToDouble(order -> order.getTotalPrice().doubleValue()).sum()));
            	orderResponses = orders.stream().map(this::mappingOrderToOrderResponse).toList();
            	response.setOrderList(orderResponses);
            	rtnList.add(response);
			}
            // TODO: mapping order list to order response
            
            
        } catch (ParseException e) {
        	throw new ParseException ("Parse date failed: " + e.getMessage(), 0);
        } catch (RuntimeException e) {
        	throw new RuntimeException("Error occurred in summaryOrdersByTime: " + e.getMessage());
        }
        return rtnList;
    }
    
    private OrderResponse mappingOrderToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        OrderResponse.CustomerInfo cusResponse = new OrderResponse.CustomerInfo();
        List<OrderResponse.DishResponse> dishes = new ArrayList<>();

        orderResponse.setOrderSku(order.getId());
        orderResponse.setTotalPrice(order.getTotalPrice());
        
        cusResponse.setCustomerName(order.getCustomerName());
        cusResponse.setPhone(order.getCustomerPhone());
        cusResponse.setCustomerEmail(order.getCustomerEmail());
        orderResponse.setCustomerInfo(cusResponse);
        
        dishes = order.getOrderDetails().stream().map(detail -> {
        	OrderResponse.DishResponse dish = new OrderResponse.DishResponse();
            dish.setDishName(detail.getDishName());
            dish.setQuantity(detail.getQuantity());
            dish.setPrice(detail.getPrice());
            return dish;
        }).toList();
        orderResponse.setDishes(dishes);
        
        return orderResponse;
    }
}
