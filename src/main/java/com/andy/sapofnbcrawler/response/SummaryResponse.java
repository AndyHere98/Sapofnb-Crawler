package com.andy.sapofnbcrawler.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class SummaryResponse {

	private String orderDate;
	private BigDecimal totalPrice;
	private List<OrderResponse> orderList;
}
