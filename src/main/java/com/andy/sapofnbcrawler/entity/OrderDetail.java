package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long orderId;
	@ManyToOne
	@JoinColumn(name = "order_id", referencedColumnName = "order_sku", nullable = false)
	private Order order;
	@Column(name = "dish_name", nullable = false, length = 200)
	private String name;
	@Column(length = 200)
	private String member;
	@Column(nullable = false, length = 2)
	private int quantity;
	@Column(nullable = false, length = 5)
	private BigDecimal price;
//	@Column(nullable = false)
//    private Date orderDate;
}
