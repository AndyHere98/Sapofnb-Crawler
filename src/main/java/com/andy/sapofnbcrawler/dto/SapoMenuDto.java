package com.andy.sapofnbcrawler.dto;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(
	name = "Menu được truy xuất từ Sapofnb",
	description = "Menu được truy xuất từ Sapofnb"
)
@Data
public class SapoMenuDto {


	@Schema(
		description = "Tên menu theo ngày trong tuần"
	)
    private String name;

	@Schema(
		description = "Danh sách các món ăn"
	)
    @JsonProperty("objects")
    private List<DishRequest> dishes;

	@Schema(
		name = "Thông tin món từ Sapo",
		description = "Thông tin món từ Sapo"
	)
    @Data
    public static class DishRequest {

    	@Schema(
			description = "Tên món ăn"
		)
        private String name;

    	@Schema(
			description = "Chi tiết món ăn"
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

