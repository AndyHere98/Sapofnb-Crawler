package com.andy.sapofnbcrawler.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Tag(name = "Thông tin khách hàng", description = "Nơi lưu trữ thông tin khách hàng")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class CustomerInfoDto {
	@Schema(description = "Địa chỉ IP thiết bị")
	private String ipAddress;

	@Schema(description = "Tên thiết bị")
	private String pcHostName;

	@Schema(description = "Account này có phải admin?")
	private int isAdmin;

	@Schema(description = "Tên khách hàng")
	@NotEmpty(message = "Tên khách hàng không được để trống")
	@Size(max = 200)
	private String customerName;

	@Schema(description = "Số điện thoại khách hàng")
	@NotEmpty(message = "Số điện thoại khách hàng không được để trống")
	@Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "Số điện thoại khách hàng cần đúng định dạng 10 số")
	private String customerPhone;

	@Schema(description = "Thông tin email khách hàng")
	@NotEmpty(message = "Email khách hàng không được để trống")
	private String customerEmail;

	@Schema(description = "Công nợ hiện tại dựa trên những đơn chưa thanh toán")
	private BigDecimal balance;

	@Schema(description = "Tổng đơn đã đặt")
	private int totalOrders;

	@Schema(description = "Tổng số phần đã đặt")
	private int totalDishes;

	@Schema(description = "Tổng tiền đã chi")
	private BigDecimal totalSpending;
}
