package com.andy.sapofnbcrawler.object;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillingSummary {
	private BigDecimal totalRevenue = BigDecimal.ZERO;
	private BigDecimal dailyRevenue = BigDecimal.ZERO;
	private BigDecimal monthlyRevenue = BigDecimal.ZERO;
	private BigDecimal yearlyRevenue = BigDecimal.ZERO;
}
