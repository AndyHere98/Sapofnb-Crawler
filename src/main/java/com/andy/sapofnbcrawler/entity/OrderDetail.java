package com.andy.sapofnbcrawler.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.Nationalized;

import com.andy.sapofnbcrawler.object.DailySummaryOrders;

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
import lombok.AccessLevel;
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
		+ ", o.unit_price as unitPrice "
		+ ", sum(o.quantity) as quantity "
		+ ", sum(o.total_amount) as sumPrice "
		+ "from order_detail o "
		+ "join member_order m on (m.order_sku = o.order_id) "
		+ "where m.order_date = :orderDate "
		+ " and m.order_status <> 'C'"
		+ "group by (o.dish_name, o.unit_price) "
		+ "order by dishName asc", resultSetMapping = "DailySummaryOrderMapping")
@SqlResultSetMapping(name = "DailySummaryOrderMapping", classes = @ConstructorResult(targetClass = DailySummaryOrders.class, columns = {
		@ColumnResult(name = "dishName", type = String.class),
		@ColumnResult(name = "quantity", type = Integer.class),
		@ColumnResult(name = "unitPrice", type = BigDecimal.class),
		@ColumnResult(name = "sumPrice", type = BigDecimal.class),
}))
public class OrderDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", referencedColumnName = "order_sku", nullable = false)
	private Order order;
	@Column(name = "dish_name", nullable = false, length = 200)
	@Nationalized
	private String name;
	@Column(nullable = false, length = 2)
	@Setter(AccessLevel.NONE)
	private int quantity = 0;
	@Column(name = "unit_price", nullable = false, length = 5)
	@Setter(AccessLevel.NONE)
	private BigDecimal price;
	@Column(name = "total_amount", nullable = false, length = 7)
	private BigDecimal totalAmout;

	public void setQuantity(int quantity) {
		this.quantity = quantity;
		if (this.price != null) {
			setTotalAmout(price.multiply(new BigDecimal(quantity)));
		}
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
		if (this.quantity >= 0) {
			setTotalAmout(price.multiply(new BigDecimal(quantity)));
		}
	}

	@Override
	public String toString() {
		return String.format("Món %s giá %s gồm %s phần", name, price,
				quantity);
	}

	@Override
	public boolean equals(Object order) {
		return (((OrderDetail) order).getId()) == (id);
	}

	@Override
	public int hashCode() {
		return id.intValue();
	}
}
