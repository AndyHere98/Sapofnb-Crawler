package com.andy.sapofnbcrawler.object;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillingSummary {
	private BigDecimal totalRevenue;
	private BigDecimal dailyRevenue;
	private BigDecimal monthlyRevenue;
	private BigDecimal yearlyRevenue;
}
