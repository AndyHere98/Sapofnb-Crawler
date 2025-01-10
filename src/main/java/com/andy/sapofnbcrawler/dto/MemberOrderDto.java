package com.andy.sapofnbcrawler.dto;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
	name = "Thông tin đơn hàng dựa trên mã đơn hàng",
	description = "Thông tin đơn hàng tổng hợp dựa trên mã đơn hàng"
)
@Data
@JsonInclude(Include.NON_NULL)
public class MemberOrderDto {

	@Schema(
		description = "Tên khách hàng"
	)
	private String customerName;
	
	@Schema(
		description = "Số điện thoại khách hàng"
	)
    private String customerPhone;
	
	@Schema(
		description = "Thông tin email khách hàng"
	)
    private String customerEmail;
	
	@Schema(
			description = "Tổng số lượng món"
		)
	    private int totalDish;
	
	@Schema(
		description = "Tổng chi tiêu"
	)
    private BigDecimal totalPrice;
	
	@Schema(
		description = "Danh sách các đơn hàng"
	)
	private List<OrderDto> orderList;

}

