package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 36, name = "order_sku", unique = true, updatable = false)
	private String orderSku;
	@Column(nullable = false, length = 100)
	private String customerName;
	@Column(nullable = false, length = 20)
    private String customerPhone;
	@Column(nullable = false, length = 100)
    private String customerEmail;
	@Column(length = 200)
    private String address;
	@Column(nullable = false)
    private BigDecimal totalPrice;
	@Column(nullable = false, length = 10)
    private String paymentMethodType;
	@Column(nullable = false, length = 1, columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private int isPaid;
    private Date orderDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderDetail> orderDetails;
}
