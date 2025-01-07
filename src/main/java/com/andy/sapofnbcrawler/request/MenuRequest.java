package com.andy.sapofnbcrawler.request;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Schema(
	name = "Thông tin yêu cầu cho menu hôm nay",
	description = "Thông tin yêu cầu cho menu hôm nay"
)
@Data
public class MenuRequest {


	@Schema(
		description = "Tên khách hàng"
	)
	@NotEmpty(message = "Tên khách hàng không được để trống")
	@Size(max = 200)
    private String name;

	@Schema(
		description = "Tổng hợp các món ăn"
	)
    @NotEmpty(message = "Thông tin đặt món ăn không được trống")
    @JsonProperty("objects")
    private List<DishRequest> dishes;

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
        private String name;

    	@Schema(
			description = "Danh sách chi tiết món ăn"
		)
    	@NotEmpty(message = "Danh sách chi tiết món ăn không được để trống")
        @JsonProperty("variants")
        private List<DishDetailRequest> dishDetail;
        

    	@Schema(
    		name = "Thông tin chi tiết món ăn",
    		description = "Thông tin chi tiết món ăn"
    	)
        @Data
        public static class DishDetailRequest {

        	@Schema(
    			description = "Giá món"
    		)
        	@Pattern(regexp = "^([1-9]{2})+([0]{3})+(\\.[0]{2})?$", message = "Định dạng giá món: 11000.00 đến 99000.00 hoặc 11000 đến 99000")
        	@Positive(message = "Giá món phải lớn hơn 0")
            private BigDecimal price;
        }
    }
    
}

