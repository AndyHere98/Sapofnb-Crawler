package com.andy.sapofnbcrawler.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.andy.sapofnbcrawler.dto.MemberOrderDto;
import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.exception.ResourceNotFoundException;
import com.andy.sapofnbcrawler.mapper.OrderMapper;
import com.andy.sapofnbcrawler.repository.IOrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final IOrderRepository       orderRepository;
//    private final IOrderDetailRepository orderDetailRepository;
    
    
    public List<OrderDto> summaryTodayOrder() {
        SimpleDateFormat sdf    = new SimpleDateFormat("dd/MM/yyyy");
        Date             today  = new Date();
//        List<Order>      orders = orderRepository.getOrderByOrderDate(sdf.format(today))
//        		.orElseThrow(() -> new ResourceNotFoundException("Đơn hàng hôm nay", "ngày đặt đơn", sdf.format(today)))
//        		;
//        
//        List<OrderDto> orderDtoList = OrderMapper.mappingAdminSummaryOrder(orders, new ArrayList<OrderDto>());
        return null;
    }
    
    public List<MemberOrderDto> summaryTodayOrderByMember(OrderDto orderDto) {
//        SimpleDateFormat sdf     = new SimpleDateFormat("dd/MM/yyyy");
//        SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date             today   = new Date();
        List<Order>      orders  = orderRepository.getOrdersFromDateToToDate(orderDto)
        		.orElseThrow(() -> new ResourceNotFoundException("Danh sách đơn đặt hàng", "ngày đặt ", String.format(" từ %s đến %s", orderDto.getFromDate(), orderDto.getToDate())))
        		;
        
        List<MemberOrderDto> memberOrderDtoList = OrderMapper.mappingMemberSummaryOrder(orders, new ArrayList<>());
        
        return memberOrderDtoList;
    }
    
    /*
    public List<SummaryResponse> summaryOrdersByTime(SummaryRequest request) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        
        DateTime today = DateTime.now(TimeZone.getDefault());
        DateTime diffDate = today;
        String day = "";
        
        List<Order> orderList = new ArrayList<>();
        List<OrderDto> orderResponses = new ArrayList<>();
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
                    request.getCustomerName(), day)
            		.orElseThrow(() -> new ResourceNotFoundException("Đơn hàng hôm nay", "ngày đặt đơn", sdf.format(today)))
            		;
            
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
        	throw new RuntimeException("Date is parsing to format \"dd/MM/yyyy\" failed");
        } catch (RuntimeException e) {
        	throw new RuntimeException("Error occurred in summaryOrdersByTime: " + e.getMessage());
        }
        return rtnList;
    }
    
    private OrderDto mappingOrderToOrderResponse(Order order) {
        OrderDto orderResponse = new OrderDto();
        List<OrderDetailDto> dishes = new ArrayList<>();

        orderResponse.setOrderSku(order.getOrderSku());
        orderResponse.setTotalPrice(order.getTotalPrice());
        
        orderResponse.setCustomerName(order.getCustomerName());
        orderResponse.setCustomerPhone(order.getCustomerPhone());
        orderResponse.setCustomerEmail(order.getCustomerEmail());
        
        dishes = order.getOrderDetails().stream().map(detail -> {
        	OrderDetailDto dish = new OrderDetailDto();
            dish.setName(detail.getName());
            dish.setQuantity(detail.getQuantity());
            dish.setPrice(detail.getPrice());
            return dish;
        }).toList();
        orderResponse.setOrderDetails(dishes);
        
        return orderResponse;
    }
    */

	public List<OrderDto> summaryOrders(OrderDto orderDto) {

		List<Order> orderList = orderRepository.getOrdersFromDateToToDate(orderDto)
				.orElseThrow(() -> new ResourceNotFoundException("Danh sách đơn đặt hàng", "ngày đặt ", String.format(" từ %s đến %s", orderDto.getFromDate(), orderDto.getToDate())));
		
		return OrderMapper.mappingAdminSummaryOrder(orderList, new ArrayList<OrderDto>());
	}
}
