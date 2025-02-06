package com.andy.sapofnbcrawler.mapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.andy.sapofnbcrawler.dto.CustomerInfoDto;
import com.andy.sapofnbcrawler.dto.MemberOrderDto;
import com.andy.sapofnbcrawler.dto.OrderDetailDto;
import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.dto.OrderSummaryDto;
import com.andy.sapofnbcrawler.dto.OrderSummaryDto.DailyOrderSummary;
import com.andy.sapofnbcrawler.dto.SapoOrderDto;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.entity.OrderDetail;
import com.andy.sapofnbcrawler.object.CustomerRank;
import com.andy.sapofnbcrawler.object.DailySummaryOrders;
import com.andy.sapofnbcrawler.request.MemberOrderRequest;

public class OrderMapper {

	private OrderMapper() {};

	public static OrderDto mappingToOrderDto(Order order, OrderDto orderDto) {
		OrderDetailDto orderDetailDto = new OrderDetailDto();
		List<OrderDetailDto> orderDetailDtolList = new ArrayList<>();

		BeanUtils.copyProperties(order, orderDto);

        for (int i = 0; i < order.getOrderDetails().size(); i++) {
        	orderDetailDto = new OrderDetailDto();
            BeanUtils.copyProperties(order.getOrderDetails().get(i), orderDetailDto);
            orderDetailDto.setId(order.getOrderDetails().get(i).getName().hashCode());
            orderDetailDtolList.add(orderDetailDto);
        }

        orderDto.setOrderDetails(orderDetailDtolList);
		orderDto.setCreatedOn(Timestamp.valueOf(order.getCreatedDate()).getTime());
		orderDto.setModifiedOn(Timestamp
				.valueOf((order.getUpdateDate() != null ? order.getUpdateDate() : order.getCreatedDate())).getTime());

		
		return orderDto;
	}

	public static Order mappingOrderDtoToOrder(OrderDto orderDto, Order order) {

		BeanUtils.copyProperties(orderDto, order);
		BigDecimal totalPrice = orderDto.getOrderDetails().stream().map(dish -> dish.getPrice().multiply(new BigDecimal(dish.getQuantity()))).reduce(BigDecimal.ZERO,  BigDecimal::add);
		if (totalPrice.compareTo(new BigDecimal(0)) != 1) {
    		throw new RuntimeException("Tổng giá trị đơn hàng: " + totalPrice + " phải lớn hơn 0. Vui lòng kiểm tra lại!");
    	}
		
		order.setTotalPrice(totalPrice);
		
		return order;
	}
	

	public static OrderDto mappingToOrderDtoFromSapoOrderDto(SapoOrderDto sapoOrder, OrderDto orderDto) {
		OrderDetailDto dishResponse = new OrderDetailDto();
		List<OrderDetailDto> dishes = new ArrayList<>();

		BeanUtils.copyProperties(sapoOrder, orderDto);
		orderDto.setAddress(sapoOrder.getFullAddress());

		for (int i = 0; i < sapoOrder.getOrderDetails().size(); i++) {
			dishResponse = new OrderDetailDto();
			BeanUtils.copyProperties(sapoOrder.getOrderDetails().get(i), dishResponse);
			dishes.add(dishResponse);
		}

		orderDto.setOrderDetails(dishes);
		orderDto.setCreatedOn(sapoOrder.getCreatedOn());
		orderDto.setModifiedOn(sapoOrder.getModifiedOn());

		return orderDto;
	}

	public static List<OrderDto> mappingAdminSummaryOrder(List<Order> orderList, List<OrderDto> orderDtoList) {

		Map<Date, List<Order>> orderMap = new HashMap<>();

		// Divide order list into list base on order date
		for (Order order : orderList) {
			if (orderMap.containsKey(order.getOrderDate())) {
				orderMap.get(order.getOrderDate()).add(order);
			} else {
				List<Order> orders = new ArrayList<>();
				orders.add(order);
				orderMap.put(order.getOrderDate(), orders);
			}

		}

		for (Map.Entry<Date, List<Order>> orderEntry : orderMap.entrySet()) {
			Date key = orderEntry.getKey();
			List<Order> val = orderEntry.getValue();
			int totalDish = 0;
			BigDecimal totalPrice = BigDecimal.ZERO;
			Map<String, OrderDetail> mealMap = new HashMap<>();
			
			OrderDto orderDto = new OrderDto();
			orderDto.setOrderDate(new SimpleDateFormat("dd/MM/yyyy").format(key));

			List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

			for (Order order : val) {
				for (OrderDetail orderDetail : order.getOrderDetails()) {
					if (mealMap.containsKey(orderDetail.getName())) {
						orderDetail.setQuantity(mealMap.get(orderDetail.getName()).getQuantity() + orderDetail.getQuantity());
					}
					mealMap.put(orderDetail.getName(), orderDetail);
				}
			}

			for (Map.Entry<String, OrderDetail> entry : mealMap.entrySet()) {
				OrderDetail detailVal = entry.getValue();

				OrderDetailDto orderDetailDto = new OrderDetailDto();
				orderDetailDto.setId(entry.getKey().hashCode());
				orderDetailDto.setName(detailVal.getName());
				orderDetailDto.setQuantity(detailVal.getQuantity());
				orderDetailDto.setPrice(detailVal.getPrice());
				
				totalDish += orderDetailDto.getQuantity();
				totalPrice = totalPrice.add(orderDetailDto.getPrice().multiply(new BigDecimal(orderDetailDto.getQuantity())));
				
				orderDetailDtoList.add(orderDetailDto);
			}
			
			orderDto.setTotalDishes(totalDish);
			orderDto.setTotalPrice(totalPrice);
			orderDto.setOrderDetails(orderDetailDtoList);
			orderDtoList.add(orderDto);
		}

		return orderDtoList;
	}
	
	public static List<MemberOrderDto> mappingMemberSummaryOrder(List<Order> orderList, List<MemberOrderDto> memberOrderDtoList) {
		
		Map<Integer, List<Order>> orderMap = new HashMap<>();

		// Divide order list into list base on order date
		for (Order order : orderList) {
//			String keyStr = String.format("%s%s%s", order.getCustomerName(), order.getCustomerPhone(), order.getCustomerEmail());
			String keyStr = String.valueOf(order.hashCode());
			Integer key = keyStr.hashCode();
			if (orderMap.containsKey(key)) {
				orderMap.get(key).add(order);
			} else {
				List<Order> orders = new ArrayList<>();
				orders.add(order);
				orderMap.put(key, orders);
			}
		}
		

		for (Map.Entry<Integer, List<Order>> memberOrderMap : orderMap.entrySet()) {
			
			MemberOrderDto memberOrderDto = new MemberOrderDto();
			OrderDto orderDto = new OrderDto();
			List<OrderDto> orderDtoList = new ArrayList<>();

			int totalQuantity = 0;
			BigDecimal totalPrice = BigDecimal.ZERO;
			
			
			for (Order order : memberOrderMap.getValue()) {
				int orderQuantity = 0;
				BigDecimal orderPrice = BigDecimal.ZERO;
				
				orderDto = new OrderDto();
				List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();
				
				for (OrderDetail orderDetail: order.getOrderDetails()) {
					totalQuantity += orderDetail.getQuantity();
					totalPrice = totalPrice.add(orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getQuantity())));
				
					orderQuantity += orderDetail.getQuantity();
					orderPrice = orderPrice.add(orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getQuantity())));
				

					OrderDetailDto orderDetailDto = new OrderDetailDto();
					orderDetailDto.setId((orderDetail.getName() + memberOrderMap.getKey()).hashCode());
					orderDetailDto.setName(orderDetail.getName());
					orderDetailDto.setQuantity(orderDetail.getQuantity());
					orderDetailDto.setPrice(orderDetail.getPrice());
					
					orderDetailDtoList.add(orderDetailDto);
					orderDto.setOrderDetails(orderDetailDtoList);
				}
				
				orderDto.setOrderSku(order.getOrderSku());
				orderDto.setOrderDate(new SimpleDateFormat("dd/MM/yyyy").format(order.getOrderDate()));
				orderDto.setTotalDishes(orderQuantity);
				orderDto.setTotalPrice(orderPrice);
				orderDto.setCreatedOn(Timestamp.valueOf(order.getCreatedDate()).getTime());
				orderDto.setModifiedOn(Timestamp
						.valueOf((order.getUpdateDate() != null ? order.getUpdateDate() : order.getCreatedDate())).getTime());

				orderDtoList.add(orderDto);
			}
			
			memberOrderDto.setTotalDish(totalQuantity);
			memberOrderDto.setTotalPrice(totalPrice);
			memberOrderDto.setOrderList(orderDtoList);
			
			memberOrderDtoList.add(memberOrderDto);
		}
		
		
		return memberOrderDtoList;
	}

	public static Order mappingMemberOrderRequestToOrder(MemberOrderRequest request) {
		Order order = new Order();
		BeanUtils.copyProperties(request, order);

		return order;
	}

	public static OrderDetail mappingOrderDetailDtoToOrderDetail(OrderDetailDto request) {
		OrderDetail orderDetail = new OrderDetail();
		BeanUtils.copyProperties(request, orderDetail);
		return orderDetail;
	}
	
	public static CustomerInfoDto mappingCustomerInfoDto(CustomerRank customerRank) {
		CustomerInfoDto customerInfoDto = new CustomerInfoDto();
		BeanUtils.copyProperties(customerRank, customerInfoDto);
		return customerInfoDto;
	}
	public static DailyOrderSummary mappingDailyOrderSummary(DailySummaryOrders order) {
		DailyOrderSummary orderDto = new OrderSummaryDto().new DailyOrderSummary();
		BeanUtils.copyProperties(order, orderDto);
		return orderDto;
	}
}
