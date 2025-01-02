package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
	private Long id;
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;
	@Column(nullable = false, length = 200)
	private String dishName;
	@Column(nullable = false, length = 200)
	private String member;
	@Column(nullable = false, length = 6)
	private int quantity;
	@Column(nullable = false, length = 7)
	private BigDecimal price;
	@Column(nullable = false)
    private LocalDateTime orderDate;
}
