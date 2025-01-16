package com.andy.sapofnbcrawler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(
	name = "Thông tin món ăn",
	description = "Thông tin món ăn"
)
@Data
@JsonInclude(Include.NON_NULL)
public class UserDto {
	@Schema(
		description = "Địa chỉ IP thiết bị"
	)
	private String ipAddress;

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
    @Pattern(
    	regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", 
    	message = "Số điện thoại khách hàng cần đúng định dạng 10 số"
    )
    private String customerPhone;
	
	@Schema(
		description = "Thông tin email khách hàng"
	)
	@NotEmpty(message = "Email khách hàng không được để trống")
    private String customerEmail;
}
