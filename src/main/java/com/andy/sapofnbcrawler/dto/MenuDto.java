package com.andy.sapofnbcrawler.dto;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(
	name = "Menu các món ăn trong ngày",
	description = "Menu các món ăn trong ngày"
)
@Data
@JsonInclude(Include.NON_NULL)
public class MenuDto {


    @Schema(
		description = "Tên menu dựa trên ngày trong tuần"
	)
    private String name;

    @Schema(
		description = "Tổng hợp các món ăn"
	)
    @JsonProperty("dishes")
    private List<OrderDetailDto> dishes;
    
}

