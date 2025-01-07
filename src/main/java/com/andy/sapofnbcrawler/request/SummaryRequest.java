package com.andy.sapofnbcrawler.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
	@NotEmpty(message = "Tên khách hàng không được để trống")
	@Size(max = 200)
    private String customerName;

	@Schema(
		description = "Đơn vị thời gian"
	)
	@NotEmpty(message = "Tổng hợp đơn hàng cần đơn vị thời gian để tính")
    private String unit;

	@Schema(
		description = "Khoảng thời gian"
	)
	@Pattern(regexp = "^([1-9]{1})+([0-9]{0,1})$", message = "Khoảng thời gian nhập vào cần đúng kiểu dữ liệu.")
	@NotEmpty(message = "Khoảng thời gian không được để trống")
    private int quantity;
}
