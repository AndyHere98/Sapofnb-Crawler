package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.Nationalized;

import com.andy.sapofnbcrawler.object.DailySummaryOrders;
import com.fasterxml.jackson.annotation.JsonBackReference;

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
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "order_detail")
// @Data
@NamedNativeQuery(name = "GetDailySummaryOrder", query = "select "
		+ "o.dish_name as dishName "
		+ ", sum(o.quantity) as quantity "
		+ ", sum(o.price) as sumPrice "
		+ "from order_detail o "
		+ "join member_order m on (m.order_sku = o.order_id)"
		+ "where m.order_date = :orderDate "
		+ "group by (o.dish_name)"
		+ "order by dishName asc", resultSetMapping = "DailySummaryOrderMapping")
@SqlResultSetMapping(name = "DailySummaryOrderMapping", classes = @ConstructorResult(targetClass = DailySummaryOrders.class, columns = {
		@ColumnResult(name = "dishName", type = String.class),
		@ColumnResult(name = "quantity", type = Integer.class),
		@ColumnResult(name = "sumPrice", type = BigDecimal.class),
}))
public class OrderDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", referencedColumnName = "order_sku", nullable = false)
	@JsonBackReference
	private Order order;
	@Column(name = "dish_name", nullable = false, length = 200)
	@Nationalized
	private String name;
	@Column(nullable = false, length = 2)
	private int quantity;
	@Column(nullable = false, length = 5)
	private BigDecimal price;
}
