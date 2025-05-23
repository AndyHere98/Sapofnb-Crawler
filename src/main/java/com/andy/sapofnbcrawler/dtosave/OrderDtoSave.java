package com.andy.sapofnbcrawler.dtosave;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDtoSave {

	private Long id;
	private String orderSku;
	private String note;
	private BigDecimal totalPrice;
	private int totalDishes;
	private String paymentMethod;
	private String paymentType;
	private int isPaid;
	private Date orderDate;
	private String orderStatus;
	private String cancelReason;
}
