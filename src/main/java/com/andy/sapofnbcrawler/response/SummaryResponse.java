package com.andy.sapofnbcrawler.response;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(
	name = "Tổng hợp đơn đã đặt",
	description = "Tổng hợp đơn đã đặt"
)
@Data
public class SummaryResponse {


    @Schema(
		description = "Ngày đặt đơn"
	)
	private String orderDate;

	@Schema(
		description = "Tổng giá trị các đơn hàng"
	)
	private BigDecimal totalPrice;

	@Schema(
		description = "Danh sách các đơn hàng"
	)
	private List<OrderResponse> orderList;
}
