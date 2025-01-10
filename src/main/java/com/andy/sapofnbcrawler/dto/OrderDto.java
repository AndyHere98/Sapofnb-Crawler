package com.andy.sapofnbcrawler.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "Thông tin đơn hàng", description = "Thông tin đơn hàng")
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({ "customerInfo", "orderSku", "status", "fullAddress", "note", "orderDate", "orderTime",
//		"updatedOrderTime", "totalPrice", "paymentMethodType", "paymentMethodName", "dishes" })
@Data
public class OrderDto {

	@Schema(
		description = "Tên khách hàng"
	)
	@NotEmpty(message = "Tên khách hàng không được để trống")
	@Size(max = 200)
	private String customerName;
	
	@Schema(
		description = "Số điện thoại khách hàng"
	)
	@NotEmpty(message = "Số điện thoại khách hàng không được để trống")
    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$")
    private String customerPhone;
	
	@Schema(
		description = "Thông tin email khách hàng"
	)
    private String customerEmail;

	@Schema(description = "Mã đơn hàng")
	private String orderSku;

	@Schema(description = "Ghi chú đến bếp")
	private String note;

	@Schema(description = "Địa chỉ giao hàng")
	private String address;

	@Schema(description = "Trạng thái đơn hàng")
	private String status;

	@Schema(description = "Thời gian tạo đơn hàng")
	@JsonProperty("orderTime")
	private long createdOn;

	@Schema(description = "Thời gian đơn hàng cập nhật trạng thái lần cuối")
	@JsonProperty("updatedOrderTime")
	private long modifiedOn;

	@Schema(description = "Ngày đặt đơn")
	private String orderDate;

	@Schema(description = "Tổng số phần đã đặt")
	private int totalDish;

	@Schema(description = "Tổng giá trị đơn hàng")
	private BigDecimal totalPrice;

	@Schema(description = "Tổng hợp các món ăn")
	@JsonProperty("orderDetails")
	private List<OrderDetailDto> orderDetails;

	@Schema(description = "Hình thức thanh toán dạng code")
	private String paymentMethodType;

	@Schema(description = "Hình thức thanh toán dạng chữ")
	private String paymentMethodName;

	@Schema(description = "Đơn hàng từ ngày")
	private String fromDate;

	@Schema(description = "Đơn hàng đến ngày")
	private String toDate;
}
