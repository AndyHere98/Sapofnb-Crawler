package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@Data
@Getter
@Setter
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
//	@Column(nullable = false, length = 20)
	@Column(length = 20)
    private String customerPhone;
//	@Column(nullable = false, length = 100)
	@Column(length = 100)
    private String customerEmail;
//	@Column(nullable = false, length = 36, name = "customer", unique = true, updatable = false)
//	private int customerCode;
	@Column(length = 200)
    private String note;
	@Column(nullable = false)
    private BigDecimal totalPrice;
	@Column(nullable = false, length = 10)
    private String paymentMethod;
	@Column(nullable = false, length = 1, columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private int isPaid;
    private Date orderDate;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order", orphanRemoval = true)
//    @JsonManagedReference
//    @JoinColumn(name = "order_sku")
    private List<OrderDetail> orderDetails = new ArrayList<>();
    
    public void addOrderDetail(List<OrderDetail> orderDetailList) {
    	orderDetailList.forEach(orderDetail -> orderDetail.setOrder(this));
    	this.orderDetails.addAll(orderDetailList);
    }
    
    public void removeOrderDetail(List<OrderDetail> orderDetailList) {
    	orderDetailList.forEach(orderDetail -> orderDetail.setOrder(null));
    	this.getOrderDetails().clear();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
    	return "Set order details: " + List.of(orderDetails);
    }
}
