package com.andy.sapofnbcrawler.object;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DailySummaryOrders {
	private String dishName;
	private int quantity;
	private BigDecimal unitPrice;
	private BigDecimal sumPrice;
	
	private String orderDate;
	private int totalDishes;
}
