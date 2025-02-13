package com.andy.sapofnbcrawler.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.dto.AdminBillingSummaryDto;
import com.andy.sapofnbcrawler.dto.AdminCustomerSummaryDto;
import com.andy.sapofnbcrawler.dto.AdminOrderSummaryDto;
import com.andy.sapofnbcrawler.dto.AdminOrderSummaryDto.DailyOrderStat;
import com.andy.sapofnbcrawler.dto.CustomerInfoDto;
import com.andy.sapofnbcrawler.dto.MemberOrderDto;
import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.dto.AdminBillingSummaryDto.RevenueStat;
import com.andy.sapofnbcrawler.dto.OrderSummaryDto.DailyOrderSummary;
import com.andy.sapofnbcrawler.entity.CustomerInfo;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.exception.OrderCompletedException;
import com.andy.sapofnbcrawler.exception.ResourceNotFoundException;
import com.andy.sapofnbcrawler.mapper.OrderMapper;
import com.andy.sapofnbcrawler.object.BillingSummary;
import com.andy.sapofnbcrawler.object.CustomerRank;
import com.andy.sapofnbcrawler.object.DailySummaryOrders;
import com.andy.sapofnbcrawler.object.OrderSummary;
import com.andy.sapofnbcrawler.repository.ICustomerRepository;
import com.andy.sapofnbcrawler.repository.IOrderRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final HttpServletRequest httpRequest;
	private final IOrderRepository orderRepository;
	private final ICustomerRepository customerRepository;

	public AdminOrderSummaryDto summaryOrders() {
		AdminOrderSummaryDto orderSummaryDto = new AdminOrderSummaryDto();
		List<Order> orders = orderRepository.findAll();

		OrderSummary summaryOrder = orderRepository.summaryOrders();
		orderSummaryDto.setTotalOrders(summaryOrder.getTotalOrders());
		orderSummaryDto.setPendingOrders(summaryOrder.getPendingOrders());
		orderSummaryDto.setCompletedOrders(summaryOrder.getCompletedOrders());
		orderSummaryDto.setCancelledOrders(summaryOrder.getCancelledOrders());

		List<DailySummaryOrders> oDailySummaryOrders = orderRepository.summaryDailyOrders();
		List<DailyOrderStat> dailyOrderSummaries = oDailySummaryOrders.stream().map(
				order -> {
					DailyOrderStat dailyOrder = new AdminOrderSummaryDto().new DailyOrderStat();
					dailyOrder.setDate(order.getOrderDate());
					dailyOrder.setOrderCount(order.getOrderCount());
					dailyOrder.setTotalAmount(order.getSumPrice());
					return dailyOrder;
				}).toList();

		List<OrderDto> orderDtos = orders.stream().map(order -> OrderMapper.mappingToOrderDto(order, new OrderDto()))
				.toList();

		orderSummaryDto.setDailyOrderStats(dailyOrderSummaries);
		orderSummaryDto.setRecentOrders(orderDtos);
		return orderSummaryDto;
	}

	public AdminCustomerSummaryDto summaryCustomers() {
		AdminCustomerSummaryDto customerSummaryDto = new AdminCustomerSummaryDto();
		List<CustomerRank> rankList = orderRepository.rankCustomerAllTime();

		List<CustomerInfo> customerList = customerRepository.findAll();
		List<CustomerInfoDto> customerInfoDtos = customerList.stream().map(customer -> {
			CustomerInfoDto customerInfoDto = new CustomerInfoDto();
			BeanUtils.copyProperties(customer, customerInfoDto);
			BigDecimal totalDept = orderRepository.getTotalDebtOfCustomer(customer);
			customerInfoDto.setBalance(totalDept);
			return customerInfoDto;
		}).toList();

		customerSummaryDto.setTopCustomers(rankList);
		customerSummaryDto.setCustomerInfos(customerInfoDtos);

		return customerSummaryDto;
	}

	public AdminBillingSummaryDto summaryBilling() {
		AdminBillingSummaryDto billingSummary = new AdminBillingSummaryDto();

		BillingSummary summaryBilling = orderRepository.summaryBilling(new Date());
		List<Order> orders = orderRepository.getUnpaidOrder();
		List<DailySummaryOrders> dailySummaryOrders = orderRepository.summaryDailyOrders();

		List<OrderDto> orderDtos = orders.stream().map(order -> OrderMapper.mappingToOrderDto(order, new OrderDto()))
				.toList();

		List<RevenueStat> revenueStats = dailySummaryOrders.stream().map(order -> {
			RevenueStat dailyStat = new AdminBillingSummaryDto().new RevenueStat();
			dailyStat.setDate(order.getOrderDate());
			dailyStat.setOrderCount(order.getOrderCount());
			dailyStat.setRevenue(order.getSumPrice());
			return dailyStat;
		}).toList();
		BeanUtils.copyProperties(summaryBilling, billingSummary);
		billingSummary.setUnpaidOrders(orderDtos);
		billingSummary.setRevenueStats(revenueStats);

		return billingSummary;
	}

	public boolean updateCustomerByAdmin(String ipAddress, CustomerInfoDto customerInfoDto) {
		CustomerInfo customerInfo = customerRepository.findCustomerByIpAddress(ipAddress).orElseThrow(
				() -> new ResourceNotFoundException("Thông tin khách hàng", "Địa chỉ IP", ipAddress));

		CustomerInfo adminInfo = customerRepository.findCustomerByIpAddress(httpRequest.getRemoteAddr()).orElseThrow(
				() -> new ResourceNotFoundException("Thông tin người quản lý", "Địa chỉ IP", ipAddress));
		customerRepository.updateCustomerByIpAddress(ipAddress, customerInfoDto, adminInfo);

		return true;
	}

	public boolean confirmPaymentOrder(String orderSku) {
		Order order = orderRepository.findByOrderCode(orderSku)
				.orElseThrow(() -> new ResourceNotFoundException("Đơn đặt hàng", "mã đơn", orderSku));

		if (order.getIsPaid() != 0) {
			throw new OrderCompletedException(
					"Đơn hàng " + orderSku + " đã thanh toán hoàn tất. Không thể tiếp tục chỉnh sửa!");
		}
		orderRepository.confirmPaymentOrder(order.getId());
		return true;
	}

	public boolean completeOrder(String orderSku) {
		Order order = orderRepository.findByOrderCode(orderSku)
				.orElseThrow(() -> new ResourceNotFoundException("Đơn đặt hàng", "mã đơn", orderSku));

		if (!order.getOrderStatus().equalsIgnoreCase(SapoConstants.ORDER_STATUS_PENDING)) {
			throw new OrderCompletedException(
					"Đơn hàng " + orderSku + " đã thanh toán hoàn tất. Không thể tiếp tục chỉnh sửa!");
		}
		orderRepository.completeOrder(order.getId());
		return true;
	}
}
