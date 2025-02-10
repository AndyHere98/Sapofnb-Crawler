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

    private final IOrderRepository orderRepository;
    // private final IOrderDetailRepository orderDetailRepository;

    public List<OrderDto> summaryTodayOrder() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        // List<Order> orders = orderRepository.getOrderByOrderDate(sdf.format(today))
        // .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng hôm nay", "ngày
        // đặt đơn", sdf.format(today)))
        // ;
        //
        // List<OrderDto> orderDtoList = OrderMapper.mappingAdminSummaryOrder(orders,
        // new ArrayList<OrderDto>());
        return null;
    }

    public List<MemberOrderDto> summaryTodayOrderByMember(OrderDto orderDto) {
        // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        // SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // Date today = new Date();
        List<Order> orders = orderRepository.getOrdersFromDateToToDate(orderDto)
                .orElseThrow(() -> new ResourceNotFoundException("Danh sách đơn đặt hàng", "ngày đặt ",
                        String.format(" từ %s đến %s", orderDto.getFromDate(), orderDto.getToDate())));

        List<MemberOrderDto> memberOrderDtoList = OrderMapper.mappingMemberSummaryOrder(orders, new ArrayList<>());

        return memberOrderDtoList;
    }

    public List<OrderDto> summaryOrders(OrderDto orderDto) {

        List<Order> orderList = orderRepository.getOrdersFromDateToToDate(orderDto)
                .orElseThrow(() -> new ResourceNotFoundException("Danh sách đơn đặt hàng", "ngày đặt ",
                        String.format(" từ %s đến %s", orderDto.getFromDate(), orderDto.getToDate())));

        return OrderMapper.mappingAdminSummaryOrder(orderList, new ArrayList<OrderDto>());
    }
}
