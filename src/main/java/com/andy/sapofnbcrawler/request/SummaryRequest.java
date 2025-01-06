package com.andy.sapofnbcrawler.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(
	name = "Thông tin yêu cầu tổng hợp đơn hàng theo khoảng thời gian",
	description = "Thông tin yêu cầu tổng hợp đơn hàng theo khoảng thời gian"
)
@Data
public class SummaryRequest {

	@Schema(
		description = "Tên khách hàng"
	)
    private String customerName;

	@Schema(
		description = "Đơn vị thời gian"
	)
    private String unit;

	@Schema(
		description = "Khoảng thời gian"
	)
    private int quantity;
}
