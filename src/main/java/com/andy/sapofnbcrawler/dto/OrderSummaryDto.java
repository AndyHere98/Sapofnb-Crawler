package com.andy.sapofnbcrawler.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "Thông tin đơn hàng", description = "Thông tin đơn hàng")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OrderSummaryDto {
	
	private List<DailyOrderSummary> dailyOrders;
	private List<YearlyOrder> yearlyOrders;
	private List<RecentOrderSummary> recentOrders;
	
	@Data
	public class DailyOrderSummary extends OrderSum {
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
