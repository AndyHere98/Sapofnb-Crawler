package com.andy.sapofnbcrawler.service;

import java.math.BigDecimal;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.common.SapoUtils;
import com.andy.sapofnbcrawler.dto.MenuDto;
import com.andy.sapofnbcrawler.dto.OrderDetailDto;
import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.dto.SapoOrderDto;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.entity.OrderDetail;
import com.andy.sapofnbcrawler.exception.ResourceNotFoundException;
import com.andy.sapofnbcrawler.mapper.OrderMapper;
import com.andy.sapofnbcrawler.repository.IOrderDetailRepository;
import com.andy.sapofnbcrawler.repository.IOrderRepository;
import com.andy.sapofnbcrawler.validation.OrderValidation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
		
        SapoOrderDto  sapoOrderDto  = (SapoOrderDto) SapoUtils.convertJsonToObject(json, SapoOrderDto.class);
        
        Date today = SapoUtils.parseDate("yyyy-MM-dd", new Date());
        // Response in order created date is Timestamp in second -> convert to Timestamp in millisecond
        Date date = new Date(sapoOrderDto.getCreatedOn() * 1000);
        Date createdDate = SapoUtils.parseDate("yyyy-MM-dd", date);
        
        if (today.getTime() == (createdDate.getTime())) return OrderMapper.mappingToOrderDtoFromSapoOrderDto(sapoOrderDto, new OrderDto());
        
        throw new ResourceNotFoundException("Thông tin giỏ hàng hôm nay", "ngày truy xuất", today.toString());
        
    }

//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean placeOrder(OrderDto request) {
    	
    	if (SapoConstants.APP_MODE_PRODUCTION.equalsIgnoreCase(mode)) {
			return SapoUtils.checkingTimeUp();
    	}
    	
    	// Valid information from order
//    	BigDecimal totalPrice = request.getTotalPrice();
    	BigDecimal sumPriceDishes = request.getOrderDetails().stream().map(dish -> dish.getPrice().multiply(new BigDecimal(dish.getQuantity()))).reduce(BigDecimal.ZERO,  BigDecimal::add);
    	
    	if (sumPriceDishes.compareTo(new BigDecimal(0)) != 1) {
    		throw new RuntimeException("Tổng giá trị đơn hàng: " + sumPriceDishes + " phải lớn hơn 0. Vui lòng kiểm tra lại!");
    	}
    	
        // Checking member has been order today or not
        Optional<Order> orderCheck = orderRepository.getOrderByCustomerNameAndCustomerPhone(request);
        OrderValidation.isOrderFinal(orderCheck, request.getCustomerName());
        
        Order order = OrderMapper.mappingOrderDtoToOrder(request, new Order());
        order.setOrderSku(UUID.randomUUID().toString());
        order.setOrderDate(new java.sql.Date(new Date().getTime()));
        List<OrderDetail> orderDetails = request.getOrderDetails().stream().map(OrderMapper::mappingOrderDetailDtoToOrderDetail).collect(
                Collectors.toList());
        OrderValidation.isNoDishes(orderDetails); // Check no dish mapped
        
        MenuDto menu = menuService.getMenu();
        List<String> todayDishes = menu.getDishes().stream().map(OrderDetailDto::getName).toList();

        if (menu.getDishes().isEmpty()) throw new ResourceNotFoundException("Danh sách món ăn", null, null);
        Map<String, BigDecimal> dishes = new HashMap<>();
        for ( OrderDetailDto dish : menu.getDishes()) {
			dishes.put(dish.getName(), dish.getPrice());
		}
        
        orderDetails.forEach(orderDetail -> {
//        	orderDetail.setOrderId(order.getOrderSku());
        	orderDetail.setOrder(order);
        	String dishName = orderDetail.getName();
        	if (dishes.get(dishName) == null)
        		throw new ResourceNotFoundException( String.format("Danh sách menu hôm nay: %s", List.of(todayDishes)), 
                        "Món", orderDetail.getName());
        	
        	if (orderDetail.getPrice().compareTo(dishes.get(dishName)) != 0)
        		throw new RuntimeException("Giá món ăn: " + dishName + " hiện tại: " + orderDetail.getPrice() + " đang không khớp với hệ thống: " + dishes.get(dishName) + ". Vui lòng cập nhật lại thông tin mới trước khi đặt đơn!");
        });
        
        orderRepository.save(order);
        orderDetails = orderDetailRepository.saveAll(orderDetails);
        
        return true;
    }
    
//    @Transactional
//    @Transactional(propagation = Propagation.REQUIRED)
//    @Async
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public boolean editOrder(String orderSku, OrderDto request) {
        
    	if (SapoConstants.APP_MODE_PRODUCTION.equalsIgnoreCase(mode)) {
			return SapoUtils.checkingTimeUp();
    	}
    	
    	BigDecimal sumPriceDishes = request.getOrderDetails().stream().map(dish -> dish.getPrice().multiply(new BigDecimal(dish.getQuantity()))).reduce(BigDecimal.ZERO,  BigDecimal::add);
    	
    	if (sumPriceDishes.compareTo(new BigDecimal(0)) != 1) {
    		throw new RuntimeException("Tổng giá trị đơn hàng: " + sumPriceDishes + " phải lớn hơn 0. Vui lòng kiểm tra lại!");
    	}
    	
        Order order = orderRepository.findByOrderCode(orderSku).orElseThrow(
        			() -> new ResourceNotFoundException("Đơn đặt hàng", "mã đơn", orderSku)
        		);
//        orderDetailRepository.deleteAll(order.getOrderDetails());
//        order.getOrderDetails().forEach(orderDetail -> orderDetail.setOrder(null));

        Order updateOrder = OrderMapper.mappingOrderDtoToOrder(request, order);
//        updateOrder.getOrderDetails().clear();
        

//        Order updatedOrder = orderRepository.save(orderUpdate);

        MenuDto menu        = menuService.getMenu();
        
        List<OrderDetail> orderDetails = request.getOrderDetails().stream().map(OrderMapper::mappingOrderDetailDtoToOrderDetail).collect(
                Collectors.toList());
        OrderValidation.isNoDishes(orderDetails); // Check no dish mapped
        
        List<String> todayDishes = menu.getDishes().stream().map(OrderDetailDto::getName).toList();
        
        if (menu.getDishes().isEmpty()) throw new ResourceNotFoundException("Danh sách món ăn", null, null);
        Map<String, BigDecimal> dishes = new HashMap<>();
        for ( OrderDetailDto dish : menu.getDishes()) {
			dishes.put(dish.getName(), dish.getPrice());
		}

        updateOrder.getOrderDetails().clear();
        orderDetails.forEach(orderDetail -> {
//        	orderDetail.setOrderId(orderSku);
        	orderDetail.setOrder(updateOrder);
        	String dishName = orderDetail.getName();
        	if (dishes.get(dishName) == null)
        		throw new ResourceNotFoundException( String.format("Danh sách menu hôm nay: %s", List.of(todayDishes)), 
                        "Món", orderDetail.getName());
        	
        	if (orderDetail.getPrice().compareTo(dishes.get(dishName)) != 0)
        		throw new RuntimeException("Giá món ăn: " + dishName + " hiện tại: " + orderDetail.getPrice() + " đang không khớp với hệ thống: " + dishes.get(dishName) + ". Vui lòng cập nhật lại thông tin mới trước khi đặt đơn!");
        });
//        order.addOrderDetail(orderDetails);

//        order.addOrderDetail(orderDetails);
        updateOrder.setTotalPrice(sumPriceDishes);
//        updateOrder.setOrderDetails(orderDetails);
        
//        updateOrder.addOrderDetail(orderDetails);
//        orderDetails.forEach(orderDetailRepository::save);
        orderRepository.save(updateOrder);
        orderDetails = orderDetailRepository.saveAll(orderDetails);
        
        return true;
    }
    
    private void deleteOrderDetailByOrder(Order order) {
//        orderDetailRepository.deleteOrderDetailByOrder(order);
    }
    
    private void deleteOrder(Order order) {
    	orderRepository.delete(order);
    }
    
    public OrderDto getOrderById(String orderSku) {
        Order order = orderRepository.findByOrderCode(orderSku).orElseThrow(
    			() -> new ResourceNotFoundException("Đơn đặt hàng", "mã đơn", orderSku)
    		);
        
//        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);
//        order.setOrderDetails(orderDetails);
        return OrderMapper.mappingToOrderDto(order, new OrderDto());
    }

//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean deleteOrder(String orderSku) {

    	if (SapoConstants.APP_MODE_PRODUCTION.equalsIgnoreCase(mode)) {
			return SapoUtils.checkingTimeUp();
    	}
        
        Order order = orderRepository.findByOrderCode(orderSku).orElseThrow(
    			() -> new ResourceNotFoundException("Đơn đặt hàng", "mã đơn", orderSku)
    		);
//        orderDetailRepository.deleteOrderDetailByOrder(order);
        orderRepository.deleteById(order.getId());
        return true;
        
//        OrderResponse orderResponse = new OrderResponse();
//        orderResponse.setOrderSku(order.getOrderCode());
//        orderResponse.setTotalPrice(order.getTotalPrice());
//        return orderResponse;
    }

	public List<OrderDto> getOrdersWithCondition(String customerName, String fromDate, String toDate) {
		OrderDto orderDto = new OrderDto();
		orderDto.setCustomerName(customerName);
		orderDto.setFromDate(fromDate);
		orderDto.setToDate(toDate);
		
		
		List<Order> orderList = orderRepository.getOrdersFromDateToToDate(orderDto)
				.orElseThrow(() -> new RuntimeException("Không có đơn hàng được tìm thấy"))
				;
		List<OrderDto> orderDtoList = orderList.stream().map(order -> OrderMapper.mappingToOrderDto(order, new OrderDto())).toList();
		return orderDtoList;
	}

	public List<OrderDto> getAllOrders() {
		
		
		List<Order> orderList = orderRepository.findAll();
		List<OrderDto> orderDtoList = orderList.stream().map(order -> OrderMapper.mappingToOrderDto(order, new OrderDto())).toList();
		return orderDtoList;
	}
    
}
