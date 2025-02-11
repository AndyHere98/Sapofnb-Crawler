package com.andy.sapofnbcrawler.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;


@Data
public class AdminOrderSummaryDto {

	private int totalOrders;
	private int pendingOrders;
	private int completedOrders;
	private int cancelledOrders;
	
	private List<DailyOrderStat> dailyOrderStats;
	private List<OrderDto> recentOrders;
	
	@Data
	public class DailyOrderStat {
		private String date;
		private int orderCount;
		private BigDecimal totalAmount;
	}
}
