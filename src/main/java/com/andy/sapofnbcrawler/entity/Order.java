package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Nationalized;

import com.andy.sapofnbcrawler.object.BillingSummary;
import com.andy.sapofnbcrawler.object.CustomerRank;
import com.andy.sapofnbcrawler.object.DailySummaryOrders;
import com.andy.sapofnbcrawler.object.OrderSummary;
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
import lombok.EqualsAndHashCode;
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
		@NamedNativeQuery(name = "summaryDailyOrders", query = "select "
				+ "to_char(MO.ORDER_DATE, 'dd/MM/yyyy') AS orderDate "
				+ ", SUM(mo.total_price) AS sumPrice"
				+ ", count(mo.id) AS orderCount "
				+ "from MEMBER_ORDER mo "
				+ "GROUP BY (to_char(MO.ORDER_DATE, 'dd/MM/yyyy'))", resultSetMapping = "SummaryDailyOrders"),
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
		@NamedNativeQuery(name = "summaryBilling", query = "select "
				+ "(select SUM(total_price) from MEMBER_ORDER) AS totalRevenue "
				+ ", (select SUM(total_price) from MEMBER_ORDER where to_char(order_date, 'dd/MM/yyyy') = to_char(:date, 'dd/MM/yyyy')) AS dailyRevenue"
				+ ", (select SUM(total_price) from MEMBER_ORDER where to_char(order_date, 'MM/yyyy') = to_char(:date, 'MM/yyyy')) AS monthlyRevenue "
				+ ", (select SUM(total_price) from MEMBER_ORDER where to_char(order_date, 'yyyy') = to_char(:date, 'yyyy')) AS yearlyRevenue "
				+ "from dual", resultSetMapping = "SummaryBilling"),
		@NamedNativeQuery(name = "summaryOrders", query = "select "
				+ "(select count(*) from MEMBER_ORDER) AS totalOrders "
				+ ", (select count(*) from MEMBER_ORDER where order_status = 'P') AS pendingOrders "
				+ ", (select count(*) from MEMBER_ORDER where order_status = 'S') AS completedOrders "
				+ ", (select count(*) from MEMBER_ORDER where order_status = 'C') AS cancelledOrders "
				+ "from dual", resultSetMapping = "SummaryOrder"),
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
				@ColumnResult(name = "orderCount", type = Integer.class),
				@ColumnResult(name = "sumPrice", type = BigDecimal.class),
				@ColumnResult(name = "orderDate", type = String.class),
		})),
		@SqlResultSetMapping(name = "SummaryBilling", classes = @ConstructorResult(targetClass = BillingSummary.class, columns = {
				@ColumnResult(name = "totalRevenue", type = BigDecimal.class),
				@ColumnResult(name = "dailyRevenue", type = BigDecimal.class),
				@ColumnResult(name = "monthlyRevenue", type = BigDecimal.class),
				@ColumnResult(name = "yearlyRevenue", type = BigDecimal.class),
		})),
		@SqlResultSetMapping(name = "SummaryOrder", classes = @ConstructorResult(targetClass = OrderSummary.class, columns = {
				@ColumnResult(name = "totalOrders", type = Integer.class),
				@ColumnResult(name = "pendingOrders", type = Integer.class),
				@ColumnResult(name = "completedOrders", type = Integer.class),
				@ColumnResult(name = "cancelledOrders", type = Integer.class),
		})),
})
@EqualsAndHashCode
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
	@Column(nullable = false, length = 1)
	private String orderStatus;
	@Column(length = 200)
	@Nationalized
	private String cancelReason;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
	private List<OrderDetail> orderDetails = new ArrayList<>();

	@Override
	public String toString() {
		return String.format("Đơn hàng %s tạo ngày %s gồm %s món", orderSku, orderDate.toString(),
				String.valueOf(orderDetails.size()));
	}

	@Override
	public boolean equals(Object order) {
		return (((Order) order).getOrderSku()).equalsIgnoreCase(orderSku);
	}

	@Override
	public int hashCode() {
		return id.intValue() + orderSku.hashCode();
	}
}
