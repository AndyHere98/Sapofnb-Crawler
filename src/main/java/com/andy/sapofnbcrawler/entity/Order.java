package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.andy.sapofnbcrawler.object.CustomerRank;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
// @Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_order")
@NamedNativeQuery(name = "getRankingCustomer", query = "select "
		+ "c.customer_name as customerName "
		+ ", c.customer_phone as customerPhone "
		+ ", c.customer_email as customerEmail "
		+ ", sum(o.total_dishes) as totalDishes "
		+ ", sum(o.total_price) as totalSpending "
		+ ", count(o.id) as totalOrders "
		+ "from member_order o "
		+ "join customer_info c on (c.id = o.customer_id) "
		+ "where o.order_date >= :start "
		+ "and o.order_date <= :end "
		+ "group by "
		+ "(c.customer_name "
		+ ", c.customer_phone "
		+ ", c.customer_email) "
		+ "order by totalSpending desc", resultSetMapping = "CustomerRank")
@SqlResultSetMappings({
		@SqlResultSetMapping(name = "CustomerRank", classes = @ConstructorResult(targetClass = CustomerRank.class, columns = {
				@ColumnResult(name = "customerName", type = String.class),
				@ColumnResult(name = "customerPhone", type = String.class),
				@ColumnResult(name = "customerEmail", type = String.class),
				@ColumnResult(name = "totalDishes", type = Integer.class),
				@ColumnResult(name = "totalSpending", type = BigDecimal.class),
				@ColumnResult(name = "totalOrders", type = Integer.class),
		})),
})
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 36, name = "order_sku", unique = true, updatable = false)
	private String orderSku;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "customer_id", nullable = false)
	private CustomerInfo customerId;
	@Column(length = 200)
	private String note;
	@Column(nullable = false)
	private BigDecimal totalPrice;
	@Column(nullable = false, length = 2)
	private int totalDishes;
	@Column(nullable = false, length = 10)
	private String paymentMethod;
	@Column(nullable = false, length = 1, columnDefinition = "NUMBER(1,0) DEFAULT 0")
	private int isPaid;
	private Date orderDate;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
	@JsonManagedReference
	private List<OrderDetail> orderDetails = new ArrayList<>();

	public void addOrderDetail(List<OrderDetail> orderDetailList) {
		orderDetailList.forEach(orderDetail -> orderDetail.setOrder(this));
		this.orderDetails.addAll(orderDetailList);
	}

	public void removeOrderDetail(List<OrderDetail> orderDetailList) {
		orderDetailList.forEach(orderDetail -> orderDetail.setOrder(null));
		this.getOrderDetails().clear();
	}
	//
	// @Override
	// public boolean equals(Object o) {
	// if (this == o) return true;
	// if (o == null || getClass() != o.getClass()) return false;
	// Order order = (Order) o;
	// return Objects.equals(id, order.getId());
	// }
	//
	// @Override
	// public int hashCode() {
	// return Objects.hash(id);
	// }
	//
	// @Override
	// public String toString() {
	// return "Set order details: " + List.of(orderDetails);
	// }
}
