package com.andy.sapofnbcrawler.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderSummary {

	private int totalOrders;
	private int pendingOrders;
	private int completedOrders;
	private int cancelledOrders;
}
