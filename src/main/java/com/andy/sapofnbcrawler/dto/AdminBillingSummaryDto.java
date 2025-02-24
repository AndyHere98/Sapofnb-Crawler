package com.andy.sapofnbcrawler.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class AdminBillingSummaryDto {

	private BigDecimal totalRevenue = BigDecimal.ZERO;
	private BigDecimal dailyRevenue = BigDecimal.ZERO;
	private BigDecimal monthlyRevenue = BigDecimal.ZERO;
	private BigDecimal yearlyRevenue = BigDecimal.ZERO;

	private List<RevenueStat> revenueStats;
	private List<OrderDto> unpaidOrders;

	@Data
	public class RevenueStat {
		private String date;
		private int orderCount;
		private BigDecimal revenue;
	}
}
