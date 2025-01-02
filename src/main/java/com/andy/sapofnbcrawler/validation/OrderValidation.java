package com.andy.sapofnbcrawler.validation;

import java.util.List;
import java.util.Optional;

import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.entity.OrderDetail;
import com.andy.sapofnbcrawler.exception.DishNotFoundException;
import com.andy.sapofnbcrawler.exception.OrderExistedException;
import com.andy.sapofnbcrawler.exception.OrderNotFoundException;

public class OrderValidation {

	private OrderValidation() {}
	
	public static Order isValidOrder(Optional<Order> order, String orderId) {
		if (!order.isPresent()) 
			throw new OrderNotFoundException("Order số: " + orderId + " không tồn tại trong hệ thống, Vui lòng kiểm tra lại");
		return order.get();
	}
	
	public static void isOrderFinal(Optional<Order> optionalOrder, String requestPerson) {
		if (optionalOrder.isPresent()) 
        	throw new OrderExistedException(requestPerson + " đã đặt đơn hôm nay, vui lòng chỉnh sửa hoặc huỷ đơn trước 9h30 AM.");
	}
	
	public static void isNoDishes(List<OrderDetail> orderDetailList) {
		if (orderDetailList.isEmpty()) 
			throw new DishNotFoundException("Danh sách món ăn đang trống");
	}
	
	public static void checkDishIsNotInMenu(List<String> todayDishes, OrderDetail orderDetail) {
		if (!todayDishes.contains(orderDetail.getDishName())) throw new DishNotFoundException(
                "Món " + orderDetail.getDishName() + " không nằm trong danh sách menu hôm nay: " + List.of(todayDishes));
	}
}
