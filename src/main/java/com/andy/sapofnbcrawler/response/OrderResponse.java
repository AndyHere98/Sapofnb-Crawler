package com.andy.sapofnbcrawler.response;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(
	name = "Thông tin đơn hàng",
	description = "Thông tin đơn hàng"
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customerInfo", "orderSku", "status", "fullAddress", "note",
    "orderDate", "orderTime", "updatedOrderTime", "totalPrice", "paymentMethodType",
    "paymentMethodName", "dishes"
})
@Data
public class OrderResponse {
    
	@Schema(
		description = "Mã đơn hàng"
	)
    private String             orderSku;

	@Schema(
		description = "Ghi chú đến bếp"
	)
    private String             note;

	@Schema(
		description = "Địa chỉ giao hàng"
	)
    private String             fullAddress;
	
	@Schema(
		description = "Trạng thái đơn hàng"
	)
    private String             status;

	@Schema(
		description = "Thời gian đặt hàng"
	)
    @JsonProperty("orderTime")
    private long          		createdOn;

	@Schema(
		description = "Thời gian đơn hàng cập nhật trạng thái lần cuối"
	)
    @JsonProperty("updatedOrderTime")
    private long          		modifiedOn;

	@Schema(
		description = "Tổng giá trị đơn hàng"
	)
    private String             orderDate;

	@Schema(
		description = "Tổng giá trị đơn hàng"
	)
    private BigDecimal         totalPrice;

	@Schema(
		description = "Tổng hợp các món ăn"
	)
    @JsonProperty("dishes")
    private List<DishResponse> dishes;

	@Schema(
		description = "Thông tin khách hàng"
	)
    private CustomerInfo       customerInfo;

	@Schema(
		description = "Hình thức thanh toán dạng code"
	)
    private String paymentMethodType;
	
	@Schema(
		description = "Hình thức thanh toán dạng chữ"
	)
    private String paymentMethodName;

	@Schema(
		name = "Tổng hợp các món ăn",
		description = "Tổng hợp các món ăn"
	)
    @Data
    public static class DishResponse {
    	@Schema(
			description = "Tên món ăn"
		)
    	private String dishName;
    	@Schema(
			description = "Số lượng đã đặt"
		)
    	private int quantity;
    	@Schema(
			description = "Giá món"
		)
    	private BigDecimal price;
    }
    
	@Schema(
		name = "Thông tin khách hàng",
		description = "Thông tin khách hàng"
	)
    @Data
    public static class CustomerInfo {
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
    }
}

