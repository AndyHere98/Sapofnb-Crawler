package com.andy.sapofnbcrawler.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
	name = "Thông tin món ăn",
	description = "Thông tin món ăn"
)
@Data
@JsonInclude(Include.NON_NULL)
public class OrderDetailDto {
	@Schema(
		hidden = true
	)
	private int id;
	@Schema(
		description = "Tên món ăn"
	)
	private String name;
	@Schema(
		description = "Số lượng đã đặt"
	)
	private int quantity;
	@Schema(
		description = "Giá món"
	)
	private BigDecimal price;
}
