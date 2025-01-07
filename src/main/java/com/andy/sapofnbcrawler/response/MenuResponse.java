package com.andy.sapofnbcrawler.response;


import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(
	name = "Menu các món ăn trong ngày",
	description = "Menu các món ăn trong ngày"
)
@Data
public class MenuResponse {


    @Schema(
		description = "Tên menu dựa trên ngày trong tuần"
	)
    private String name;

    @Schema(
		description = "Tổng hợp các món ăn"
	)
    @JsonProperty("dishes")
    private List<DishResponse> dishes;
    
    @Schema(
		name = "Tổng hợp các món ăn",
		description = "Tổng hợp các món ăn"
	)
    @Data
    public static class DishResponse {

    	@Schema(
			description = "Tên món ăn"
		)
        private String     name;

    	@Schema(
			description = "Giá món"
		)
        private BigDecimal price;
    }
    
}

