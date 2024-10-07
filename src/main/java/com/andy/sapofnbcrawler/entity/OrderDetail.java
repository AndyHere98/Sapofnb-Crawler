package com.andy.sapofnbcrawler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;
	@Column(nullable = false)
	private String dishName;
	private String member;
	@Column(nullable = false)
	private int quantity;
	@Column(nullable = false)
	private BigDecimal price;
	@Column(nullable = false)
    private Date orderDate;
}
