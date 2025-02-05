package com.andy.sapofnbcrawler.service;

import java.util.List;

import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.dto.OrderSummaryDto;

public interface IOrderService {
	OrderDto checkTodayOrder();
	boolean placeOrder(OrderDto request);
	boolean editOrder(String orderSku, OrderDto request);
	OrderDto getOrderById(String orderSku);
	boolean deleteOrder(String orderSku);
	List<OrderDto> getOrdersWithCondition(String customerName, String fromDate, String toDate);
	List<OrderDto> getAllOrders();
	OrderSummaryDto summaryOrder();
}
