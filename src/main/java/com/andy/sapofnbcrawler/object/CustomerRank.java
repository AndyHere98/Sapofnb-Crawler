package com.andy.sapofnbcrawler.object;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRank {
	
	private String customerName;
    private String customerPhone;
    private String customerEmail;
    private BigDecimal totalSpending;
	private int totalDishes;
	private int totalOrders;
}
