package com.andy.sapofnbcrawler.request;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String name;

	@Schema(
		description = "Tổng hợp các món ăn"
	)
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
        private String name;

    	@Schema(
			description = "Danh sách chi tiết món ăn"
		)
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
            private BigDecimal price;
        }
    }
    
}

