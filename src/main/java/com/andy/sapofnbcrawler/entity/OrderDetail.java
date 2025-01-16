package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;
import java.util.Objects;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@Data
public class OrderDetail extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long orderId;
	@ManyToOne
	@JoinColumn(name = "order_id", referencedColumnName = "order_sku", nullable = false)
//	@JsonBackReference
	private Order order;
	@Column(name = "dish_name", nullable = false, length = 200)
	private String name;
	@Column(nullable = false, length = 2)
	private int quantity;
	@Column(nullable = false, length = 5)
	private BigDecimal price;
//	@Column(nullable = false)
//    private Date orderDate;
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetail obj = (OrderDetail) o;
        return Objects.equals(obj.getOrderId(), orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order.getOrderSku() + name);
    }
    
    @Override
    public String toString() {
    	return "Order:====>>> " + order.toString();
    }
}
