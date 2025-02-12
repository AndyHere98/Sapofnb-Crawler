package com.andy.sapofnbcrawler.dtosave;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDetailDtoSave {

	private Long id;
	private String name;
	private int quantity = 0;
	private BigDecimal price;
	private BigDecimal totalAmout;

	public void setQuantity(int quantity) {
		this.quantity = quantity;
		if (this.price != null) {
			setTotalAmout(price.multiply(new BigDecimal(quantity)));
		}
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
		if (this.quantity >= 0) {
			setTotalAmout(price.multiply(new BigDecimal(quantity)));
		}
	}

}
