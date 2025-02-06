package com.andy.sapofnbcrawler.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.andy.sapofnbcrawler.dto.CustomerInfoDto;
import com.andy.sapofnbcrawler.dto.MenuDto;
import com.andy.sapofnbcrawler.dto.OrderDetailDto;
import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.dto.OrderSummaryDto;
import com.andy.sapofnbcrawler.dto.OrderSummaryDto.DailyOrderSummary;
import com.andy.sapofnbcrawler.dto.OrderSummaryDto.YearlyOrder;
import com.andy.sapofnbcrawler.dto.OrderSummaryDto.YearlyOrder.MonthlyOrderSummary;
import com.andy.sapofnbcrawler.dto.SapoOrderDto;
import com.andy.sapofnbcrawler.entity.CustomerInfo;
//import com.andy.sapofnbcrawler.dto.OrderSummaryDto.MonthlyOrderSummary;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.entity.OrderDetail;
import com.andy.sapofnbcrawler.exception.ResourceNotFoundException;
import com.andy.sapofnbcrawler.mapper.OrderMapper;
import com.andy.sapofnbcrawler.object.CustomerRank;
import com.andy.sapofnbcrawler.object.DailySummaryOrders;
import com.andy.sapofnbcrawler.repository.ICustomerRepository;
import com.andy.sapofnbcrawler.repository.IOrderDetailRepository;
import com.andy.sapofnbcrawler.repository.IOrderRepository;
import com.andy.sapofnbcrawler.validation.OrderValidation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

	private final String URI = SapoConstants.URI;
	private final String COOKIE = SapoConstants.COOKIE;

	private final MenuService menuService;
	private final IOrderRepository orderRepository;
	private final IOrderDetailRepository orderDetailRepository;
	
	private final ICustomerRepository customerRepository;

	@Value("${sapo.customer.name}")
	private String customerName;
	@Value("${sapo.customer.phone}")
	private String customerPhone;

	@Value("${sapo-mode}")
	private static String mode;

	@Override
	public OrderDto checkTodayOrder() {
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
		String cartUrl = urlBuilder.toString();
		System.out.println("Cart Check url: " + cartUrl);
		HttpHeaders httpHeaders = restTemplate.headForHeaders(cartUrl);
		httpHeaders.add("Cookie", COOKIE);

		HttpEntity<String> httpEntity = new HttpEntity<>("Andy", httpHeaders);

		ResponseEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET, httpEntity,
				String.class);

		String json = SapoUtils.getJsonData(response.getBody());
		if (json.isEmpty())
			throw new ResourceNotFoundException("Dữ liệu phản hồi", "khi lấy thông tin menu", null);

		SapoOrderDto sapoOrderDto = (SapoOrderDto) SapoUtils.convertJsonToObject(json, SapoOrderDto.class);

		Date today = SapoUtils.parseDate("yyyy-MM-dd", new Date());
		// Response in order created date is Timestamp in second -> convert to Timestamp
		// in millisecond
		Date date = new Date(sapoOrderDto.getCreatedOn() * 1000);
		Date createdDate = SapoUtils.parseDate("yyyy-MM-dd", date);

		if (today.getTime() == (createdDate.getTime()))
			return OrderMapper.mappingToOrderDtoFromSapoOrderDto(sapoOrderDto, new OrderDto());

		throw new ResourceNotFoundException("Thông tin giỏ hàng hôm nay", "ngày truy xuất", today.toString());

	}

//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	@Override
	public boolean placeOrder(OrderDto request) {

		if (SapoConstants.APP_MODE_PRODUCTION.equalsIgnoreCase(mode)) {
			return SapoUtils.checkingTimeUp();
		}

		// Valid information from order
		BigDecimal sumPriceDishes = request.getOrderDetails().stream()
				.map(dish -> dish.getPrice().multiply(new BigDecimal(dish.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (sumPriceDishes.compareTo(new BigDecimal(0)) != 1) {
			throw new RuntimeException(
					"Tổng giá trị đơn hàng: " + sumPriceDishes + " phải lớn hơn 0. Vui lòng kiểm tra lại!");
		}

		// Checking member has been order today or not
		Optional<Order> orderCheck = orderRepository.getOrderByCustomerNameAndCustomerPhone(request);
		OrderValidation.isOrderFinal(orderCheck, request.getCustomerName());

		Order order = OrderMapper.mappingOrderDtoToOrder(request, new Order());
		order.setOrderSku(UUID.randomUUID().toString());
		order.setOrderDate(new java.sql.Date(new Date().getTime()));
		List<OrderDetail> orderDetails = request.getOrderDetails().stream()
				.map(OrderMapper::mappingOrderDetailDtoToOrderDetail).collect(Collectors.toList());
		OrderValidation.isNoDishes(orderDetails); // Check no dish mapped

		MenuDto menu = menuService.getMenu();
		List<String> todayDishes = menu.getDishes().stream().map(OrderDetailDto::getName).toList();

		if (menu.getDishes().isEmpty())
			throw new ResourceNotFoundException("Danh sách món ăn", null, null);
		Map<String, BigDecimal> dishes = new HashMap<>();
		for (OrderDetailDto dish : menu.getDishes()) {
			dishes.put(dish.getName(), dish.getPrice());
		}
		
		int totalDishes = request.getOrderDetails().stream().mapToInt(orderDetail -> orderDetail.getQuantity()).sum();

		orderDetails.forEach(orderDetail -> {
//        	orderDetail.setOrderId(order.getOrderSku());
			orderDetail.setOrder(order);
			String dishName = orderDetail.getName();
			if (dishes.get(dishName) == null)
				throw new ResourceNotFoundException(String.format("Danh sách menu hôm nay: %s", List.of(todayDishes)),
						"Món", orderDetail.getName());

			if (orderDetail.getPrice().compareTo(dishes.get(dishName)) != 0)
				throw new RuntimeException("Giá món ăn: " + dishName + " hiện tại: " + orderDetail.getPrice()
						+ " đang không khớp với hệ thống: " + dishes.get(dishName)
						+ ". Vui lòng cập nhật lại thông tin mới trước khi đặt đơn!");
		});
		
		CustomerInfo customerInfo = new CustomerInfo();
		customerInfo.setCustomerName(request.getCustomerName());
		customerInfo.setCustomerPhone(request.getCustomerPhone());
		customerInfo.setCustomerEmail(request.getCustomerEmail());
		customerInfo.setIpAddress("1231");
		customerRepository.save(customerInfo);
		
		order.setCustomerId(customerInfo);
		order.setTotalDishes(totalDishes);
		
		
		orderRepository.save(order);
		orderDetails = orderDetailRepository.saveAll(orderDetails);

		return true;
	}

	@Override
	public boolean editOrder(String orderSku, OrderDto request) {

		if (SapoConstants.APP_MODE_PRODUCTION.equalsIgnoreCase(mode)) {
			return SapoUtils.checkingTimeUp();
		}

		Order order = orderRepository.findByOrderCode(orderSku)
				.orElseThrow(() -> new ResourceNotFoundException("Đơn đặt hàng", "mã đơn", orderSku));

		orderRepository.deleteById(order.getId());
		Order updateOrder = OrderMapper.mappingOrderDtoToOrder(request, new Order());

		updateOrder.setOrderDate(new java.sql.Date(new Date().getTime()));
		MenuDto menu = menuService.getMenu();

		List<String> todayDishes = menu.getDishes().stream().map(OrderDetailDto::getName).toList();

		if (menu.getDishes().isEmpty())
			throw new ResourceNotFoundException("Danh sách món ăn", null, null);
		Map<String, BigDecimal> dishes = new HashMap<>();
		for (OrderDetailDto dish : menu.getDishes()) {
			dishes.put(dish.getName(), dish.getPrice());
		}

		List<OrderDetail> orderDetails = request.getOrderDetails().stream()
				.map(OrderMapper::mappingOrderDetailDtoToOrderDetail).toList();
		OrderValidation.isNoDishes(orderDetails); // Check no dish mapped

		orderDetails.forEach(orderDetail -> {
			orderDetail.setOrder(updateOrder);
			String dishName = orderDetail.getName();
			if (dishes.get(dishName) == null)
				throw new ResourceNotFoundException(String.format("Danh sách menu hôm nay: %s", List.of(todayDishes)),
						"Món", orderDetail.getName());

			if (orderDetail.getPrice().compareTo(dishes.get(dishName)) != 0)
				throw new RuntimeException("Giá món ăn: " + dishName + " hiện tại: " + orderDetail.getPrice()
						+ " đang không khớp với hệ thống: " + dishes.get(dishName)
						+ ". Vui lòng cập nhật lại thông tin mới trước khi đặt đơn!");
		});
		orderRepository.save(updateOrder);
		orderDetails = orderDetailRepository.saveAll(orderDetails);
		return true;
	}

	@Override
	public OrderDto getOrderById(String orderSku) {
		Order order = orderRepository.findByOrderCode(orderSku)
				.orElseThrow(() -> new ResourceNotFoundException("Đơn đặt hàng", "mã đơn", orderSku));

//        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);
//        order.setOrderDetails(orderDetails);
		return OrderMapper.mappingToOrderDto(order, new OrderDto());
	}

//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	@Override
	public boolean deleteOrder(String orderSku) {

		if (SapoConstants.APP_MODE_PRODUCTION.equalsIgnoreCase(mode)) {
			return SapoUtils.checkingTimeUp();
		}

		Order order = orderRepository.findByOrderCode(orderSku)
				.orElseThrow(() -> new ResourceNotFoundException("Đơn đặt hàng", "mã đơn", orderSku));
//        orderDetailRepository.deleteOrderDetailByOrder(order);
		orderRepository.deleteById(order.getId());
		return true;

//        OrderResponse orderResponse = new OrderResponse();
//        orderResponse.setOrderSku(order.getOrderCode());
//        orderResponse.setTotalPrice(order.getTotalPrice());
//        return orderResponse;
	}

	@Override
	public List<OrderDto> getOrdersWithCondition(String customerName, String fromDate, String toDate) {
		OrderDto orderDto = new OrderDto();
		orderDto.setCustomerName(customerName);
		orderDto.setFromDate(fromDate);
		orderDto.setToDate(toDate);

		List<Order> orderList = orderRepository.getOrdersFromDateToToDate(orderDto)
				.orElseThrow(() -> new RuntimeException("Không có đơn hàng được tìm thấy"));
		List<OrderDto> orderDtoList = orderList.stream()
				.map(order -> OrderMapper.mappingToOrderDto(order, new OrderDto())).toList();
		return orderDtoList;
	}

	@Override
	public List<OrderDto> getAllOrders() {
		List<Order> orderList = orderRepository.findAll();
		List<OrderDto> orderDtoList = orderList.stream()
				.map(order -> OrderMapper.mappingToOrderDto(order, new OrderDto())).toList();
		return orderDtoList;
	}
	
	public List<OrderDto> getOrderByOrderDate(java.sql.Date orderDate) {
		List<Order> orderList = orderRepository.getOrderByOrderDate(orderDate).get();
		List<OrderDto> orderDtoList = orderList.stream()
				.map(order -> OrderMapper.mappingToOrderDto(order, new OrderDto())).toList();
		return orderDtoList;
	}

	@Override
	public OrderSummaryDto summaryOrder() {
		OrderSummaryDto orderSummaryDto = new OrderSummaryDto();
		
		DailyOrderSummary todayOrders = new OrderSummaryDto().new DailyOrderSummary();
		YearlyOrder yearlyOrders = new OrderSummaryDto().new YearlyOrder();
		MonthlyOrderSummary monthOrders = new OrderSummaryDto().new YearlyOrder().new MonthlyOrderSummary();
		
		List<DailyOrderSummary> todayOrderList = new ArrayList<>();
		List<YearlyOrder> yearOrdersList = new ArrayList<>();
		List<MonthlyOrderSummary> monthOrderList = new ArrayList<>();
		Map<String, List<Order>> monthOrdersMap = new HashMap<>();
		
		java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
		List<DailySummaryOrders> dailyOrders = orderDetailRepository.getDailySummaryOrder(today);
		todayOrderList = dailyOrders.stream().map(OrderMapper::mappingDailyOrderSummary).toList();
		orderSummaryDto.setDailyOrders(todayOrderList);
		
		List<OrderDto> todayOrdersList = getOrderByOrderDate(today);
		orderSummaryDto.setTodayOrders(todayOrdersList);
		
		List<String> yearList = orderRepository.getDistinctYearOrder();
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		
		if (yearList.isEmpty()) {
			return new OrderSummaryDto();
		};
		
		for (String year : yearList) {
			monthOrdersMap = new HashMap<>();
			monthOrderList = new ArrayList<>();
			int yearOrders = 0;
			int yearDishes = 0;
			BigDecimal yearSpending = BigDecimal.ZERO;
			List<Order> yearOrderList = orderRepository.getOrdersInYear(year);
			yearOrders = yearOrderList.size();
			
			// Mapping to monthOrderMap
			for (Order order : yearOrderList) {
				yearDishes += order.getTotalDishes();
				yearSpending = yearSpending.add(order.getTotalPrice());
				
				String month = String.valueOf(sdf.format(order.getOrderDate()));
				
				if (monthOrdersMap.get(month) == null) {
					List<Order> newOrderList = new ArrayList<>();
					newOrderList.add(order);
					monthOrdersMap.put(month, newOrderList);
				} else {
					monthOrdersMap.get(month).add(order);
				}
			}
			
			// Mapper month
			for (Map.Entry<String, List<Order>> monthMap : monthOrdersMap.entrySet()) {
				int monthTotalOrders = monthMap.getValue().size();
				int monthDishes = 0;
				BigDecimal monthSpending = BigDecimal.ZERO;
				
				List<Order> monthOrdersList = monthMap.getValue();
				for (Order order : monthOrdersList) {
					monthDishes += order.getTotalDishes();
					monthSpending = monthSpending.add(order.getTotalPrice());
				}
				
				List<CustomerRank> customerRanks = getRankingCustomer(monthMap.getValue().get(0).getOrderDate().getTime(), SapoConstants.UNIT_MONTH);
				monthOrders = new OrderSummaryDto().new YearlyOrder().new MonthlyOrderSummary();
				monthOrders.setMonth(monthMap.getKey());

				monthOrders.setTotalDish(monthDishes);
				monthOrders.setTotalOrders(monthTotalOrders);
				monthOrders.setTotalSpending(monthSpending);
				
				monthOrders.setOrderList(monthMap.getValue().stream().map(monthOrder -> OrderMapper.mappingToOrderDto(monthOrder, new OrderDto())).toList());
				List<CustomerInfoDto> customerRankList = customerRanks.stream().map(OrderMapper::mappingCustomerInfoDto).toList();
				monthOrders.setTopCustomer(customerRankList);
				
				monthOrderList.add(monthOrders);
			}
			
			yearlyOrders.setYear(year);
			yearlyOrders.setTotalDish(yearDishes);
			yearlyOrders.setTotalOrders(yearOrders);
			yearlyOrders.setTotalSpending(yearSpending);
			yearlyOrders.setMonthlyOrderSummary(monthOrderList);
			yearOrdersList.add(yearlyOrders);
		}
		
		orderSummaryDto.setYearlyOrders(yearOrdersList);
		return orderSummaryDto;
	}
	
	public List<CustomerRank> getRankingCustomer(long time, String unitCheck){
		Date dateInput = new Date(time);
		List<CustomerRank> rankList = new ArrayList<>();
		Date start = new Date();
		Date end = new Date();
		LocalDate dateConvert = SapoUtils.convertDateToLocaleDate(dateInput);
		switch (unitCheck) {
		case SapoConstants.UNIT_MONTH: {
			start = SapoUtils.convertToDateViaSqlDate(dateConvert.withDayOfMonth(1));
			end = SapoUtils.convertToDateViaSqlDate(dateConvert.withDayOfMonth(dateConvert.lengthOfMonth()));
			break;
		}
		case SapoConstants.UNIT_YEAR: {
			start = SapoUtils.convertToDateViaSqlDate(dateConvert.withDayOfYear(1));
			end = SapoUtils.convertToDateViaSqlDate(dateConvert.withDayOfYear(dateConvert.lengthOfYear()));
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + unitCheck);
		}
//		List<Map<String, CustomerRank>> objList =  orderRepository.rankCustomerInMonthAndYear(start, end);
		rankList =  orderRepository.rankCustomerInMonthAndYear(start, end);
		
		return rankList;
	}

}
