package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_order")
public class Order extends BaseEntity {
	
	@Id
	private String id;
	@Column(nullable = false, length = 200)
	private String customerName;
	@Column(nullable = false, length = 20, unique = true)
    private String customerPhone;
	@Column(nullable = false, length = 200, unique = true)
    private String customerEmail;
	@Column(nullable = false, length = 7)
    private BigDecimal totalPrice;
	@Column(nullable = false, length = 20)
    private String paymentMethodType;
	@Column(nullable = false, length = 1, columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private int isPaid;
    private LocalDateTime orderDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderDetail> orderDetails;
}
