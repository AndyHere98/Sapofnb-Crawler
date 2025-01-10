package com.andy.sapofnbcrawler.request;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
	@NotEmpty(message = "Tên khách hàng không được để trống")
	@Size(max = 200)
	private String customerName;
	
	@Schema(
		description = "Số điện thoại khách hàng"
	)
	@NotEmpty(message = "Số điện thoại khách hàng không được để trống")
    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "Định dạng số điện thoại không chính xác, cần nhập đủ 10 số")
	private String customerPhone;

	@Schema(
		description = "Thông tin email khách hàng"
	)
	@Size(max = 200)
	@Email(message = "Email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String customerEmail;

	@Schema(
		description = "Tổng giá trị đơn hàng"
	)
//	@Pattern(regexp = "^([1-9]{1})+([0-9]{1,2})+([0]{3})+(\\.[0]{2})?$", message = "Định dạng tổng giá món: 10000.00 đến 999000.00 hoặc 10000 đến 999000")
	@NotNull(message = "Tổng giá trị đơn hàng không được để trống")
	@Positive(message = "Tổng giá trị đơn hàng phải lớn hơn 0")
	private BigDecimal totalPrice;
    
    @Schema(
		description = "Tổng hợp các món ăn"
	)
    @NotEmpty(message = "Thông tin đặt món ăn không được trống")
    @Valid
    @JsonProperty("orderItems")
    private List<DishRequest> dishes;

	@Schema(
		description = "Hình thức thanh toán dạng code"
	)
	@NotEmpty(message = "Vui lòng chọn hình thức thanh toán")
	@Size(max = 10)
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
    	@NotEmpty(message = "Tên món ăn không được để trống")
    	@Size(max = 200)
        @JsonProperty("name")
    	private String dishName;

    	@Schema(
			description = "Số lượng cần đặt"
		)
//    	@Pattern(regexp = "^([0-9]{1,2})+(\\.[0]{2})?$", message = "Số lượng nhập vào cần đúng kiểu dữ liệu. Ví dụ: 1 hoặc 1.00, tối đa 99.00")
    	@NotNull(message = "Số lượng món cần được khai báo")
    	@Positive(message = "Số lượng phải lớn hơn 0")
    	private int quantity;

    	@Schema(
			description = "Giá món"
		)
//    	@Pattern(regexp = "^([1-9]{2})+([0]{3})+(\\.[0]{2})?$", message = "Định dạng giá món: 11000.00 đến 99000.00 hoặc 11000 đến 99000")
    	@NotNull(message = "Giá món cần được khai báo")
    	@Positive(message = "Giá món phải lớn hơn 0")
    	private BigDecimal price;
    }
}

