package com.andy.sapofnbcrawler.request;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
	name = "Thông tin đơn cần đặt",
	description = "Thông tin đơn hàng"
)
@Data
public class MemberOrderRequest {

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
		description = "Tổng giá trị đơn hàng"
	)
    private BigDecimal totalPrice;
    
    @Schema(
		description = "Tổng hợp các món ăn"
	)
    @JsonProperty("orderItems")
    private List<DishRequest> dishes;

	@Schema(
		description = "Hình thức thanh toán dạng code"
	)
    @JsonProperty("paymentMethod")
    private String paymentMethodType;

	@Schema(
		name = "Tổng hợp các món ăn",
		description = "Tổng hợp các món ăn"
	)
    @Data
    public static class DishRequest {

    	@Schema(
			description = "Tên món ăn"
		)
        @JsonProperty("name")
    	private String dishName;

    	@Schema(
			description = "Số lượng cần đặt"
		)
    	private int quantity;

    	@Schema(
			description = "Giá món"
		)
    	private BigDecimal price;
    }
}

