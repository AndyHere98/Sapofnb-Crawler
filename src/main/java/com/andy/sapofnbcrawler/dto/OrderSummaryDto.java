package com.andy.sapofnbcrawler.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "Thông tin đơn hàng", description = "Thông tin đơn hàng")
@Data
public class OrderSummaryDto {

	private List<OrderDto> todayOrders = new ArrayList<>();
	private List<DailyOrderSummary> dailyOrders= new ArrayList<>();
	private List<YearlyOrder> yearlyOrders= new ArrayList<>();
	private List<RecentOrderSummary> recentOrders= new ArrayList<>();
	
	@Data
	public class DailyOrderSummary {
		private String dishName;
		private int quantity;
		private BigDecimal sumPrice;
	}
	
	@Data
	public class YearlyOrder extends OrderSum {
		private String year;
		private List<MonthlyOrderSummary> monthlyOrderSummary;
		
		@Data
		public class MonthlyOrderSummary extends OrderSum {
			private String month;
			private List<OrderDto> orderList;
			private List<CustomerInfoDto> topCustomer;
		}
	}
	
	@Data
	public abstract class OrderSum {
		private BigDecimal totalSpending;
		private int totalDish;
		private int totalOrders;
	}
	
	@Data
	public class RecentOrderSummary {
		private String orderDate;
		private List<OrderDto> orders;
	}
}
