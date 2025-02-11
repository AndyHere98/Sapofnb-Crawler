package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Nationalized;

import com.andy.sapofnbcrawler.object.CustomerRank;
import com.andy.sapofnbcrawler.object.DailySummaryOrders;
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
import jakarta.persistence.NamedNativeQueries;
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
@NamedNativeQueries({
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
			+ "order by totalSpending desc", resultSetMapping = "CustomerRank"),
	@NamedNativeQuery(name = "summaryDailyOrders"
	, query = "select "
			+ "to_char(MO.ORDER_DATE, 'dd/MM/yyyy') AS orderDate "
			+ ", SUM(mo.total_price) AS sumPrice"
			+ ", SUM(mo.total_dishes) AS totalDishes "
			+ "from MEMBER_ORDER mo "
			+ "GROUP BY (to_char(MO.ORDER_DATE, 'dd/MM/yyyy'))"
			, resultSetMapping = "SummaryDailyOrders"),
	@NamedNativeQuery(name = "getRankingCustomerAllTime", query = "select "
			+ "c.customer_name as customerName "
			+ ", c.customer_phone as customerPhone "
			+ ", c.customer_email as customerEmail "
			+ ", sum(o.total_dishes) as totalDishes "
			+ ", sum(o.total_price) as totalSpending "
			+ ", count(o.id) as totalOrders "
			+ "from member_order o "
			+ "join customer_info c on (c.id = o.customer_id) "
			+ "group by "
			+ "(c.customer_name "
			+ ", c.customer_phone "
			+ ", c.customer_email) "
			+ "order by totalSpending desc", resultSetMapping = "CustomerRank"),
})

@SqlResultSetMappings({
		@SqlResultSetMapping(name = "CustomerRank", classes = @ConstructorResult(targetClass = CustomerRank.class, columns = {
				@ColumnResult(name = "customerName", type = String.class),
				@ColumnResult(name = "customerPhone", type = String.class),
				@ColumnResult(name = "customerEmail", type = String.class),
				@ColumnResult(name = "totalDishes", type = Integer.class),
				@ColumnResult(name = "totalSpending", type = BigDecimal.class),
				@ColumnResult(name = "totalOrders", type = Integer.class),
		})),
		@SqlResultSetMapping(name = "SummaryDailyOrders", classes = @ConstructorResult(targetClass = DailySummaryOrders.class, columns = {
				@ColumnResult(name = "totalDishes", type = Integer.class),
				@ColumnResult(name = "sumPrice", type = BigDecimal.class),
				@ColumnResult(name = "orderDate", type = String.class),
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
	@Nationalized
	private String note;
	@Column(nullable = false)
	private BigDecimal totalPrice;
	@Column(nullable = false, length = 2)
	private int totalDishes;
	@Column(nullable = false, length = 10)
	private String paymentMethod;
	@Column(nullable = false, length = 10)
	private String paymentType;
	@Column(nullable = false, length = 1, columnDefinition = "NUMBER(1,0) DEFAULT 0")
	private int isPaid;
	private Date orderDate;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
	@JsonManagedReference
	private List<OrderDetail> orderDetails = new ArrayList<>();

}
