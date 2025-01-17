package com.andy.sapofnbcrawler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.request.MemberOrderRequest;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {
    
//    @Query(value = "select o from Order o " +
//                   "where FORMATDATETIME(o.orderDate, 'yyyy-MM-dd') = FORMATDATETIME(PARSEDATETIME(:today, 'yyyy-MM-dd'), 'yyyy-MM-dd')")
    @Query(value = "select o from Order o " +
            "where TO_CHAR(o.orderDate, 'yyyy-MM-dd') = TO_CHAR(TO_DATE(:today, 'yyyy-MM-dd'), 'yyyy-MM-dd')")
    Optional<List<Order>> getOrderByOrderDate(@Param("today") String today);
    
//    @Query(value = "select o from Order o " +
//                   "where FORMATDATETIME(o.orderDate, 'yyyy-MM-dd') = FORMATDATETIME(PARSEDATETIME(:today, 'yyyy-MM-dd'), 'yyyy-MM-dd')" +
//                   "order by o.customerName")
    @Query(value = "select o from Order o " +
            "where TO_CHAR(o.orderDate, 'yyyy-MM-dd') = TO_CHAR(TO_DATE(:today, 'yyyy-MM-dd'), 'yyyy-MM-dd')" +
            "order by o.customerName")
    Optional<List<Order>> getOrdersByOrderDateOrderByCustomerName(@Param("today") String today);
    
//    @Query(value = "select o from Order o where o.customerName = :#{#request.customerName}" +
//                   " and FORMATDATETIME(o.orderDate, 'yyyy-MM-dd') = FORMATDATETIME(current_date, 'yyyy-MM-dd')"
//                   )
    @Query(value = "select o from Order o where o.customerName = :#{#request.customerName}" +
            " and TO_CHAR(o.orderDate, 'yyyy-MM-dd') = TO_CHAR(current_date, 'yyyy-MM-dd')"
            )
    Optional<Order> getOrderByOrderDateOrderByCustomerName(@Param("request") MemberOrderRequest request);
    
//    @Query(value = "select o from Order o where FORMATDATETIME(o.orderDate, 'yyyy-MM-dd') >= FORMATDATETIME(PARSEDATETIME(:fromDate, 'yyyy-MM-dd'), 'yyyy-MM-dd')"
//    		+ " and o.customerName like '%' || :customerName || '%'"
//    		+ " order by o.orderDate asc, o.customerName asc")
    @Query(value = "select o from Order o where TO_CHAR(o.orderDate, 'yyyy-MM-dd') >= TO_CHAR(TO_DATE(:fromDate, 'yyyy-MM-dd'), 'yyyy-MM-dd')"
    		+ " and o.customerName like '%' || :customerName || '%'"
    		+ " order by o.orderDate asc, o.customerName asc")
    Optional<List<Order>> getOrderByOrderDateAndCustomerNameOrderByOrderDateAsc(@Param("customerName") String customerName,@Param("fromDate") String fromDate);

    @Query(value = "select o from Order o where o.orderSku = :orderSku")
	Optional<Order> findByOrderCode(@Param("orderSku") String orderSku);

    @Query(value = "select o from Order o " +
            "where TO_CHAR(o.orderDate, 'yyyy-MM-dd') = TO_CHAR(TO_DATE(:#{#orderDto.orderDate}, 'yyyy-MM-dd'), 'yyyy-MM-dd')" +
    		" and o.customerName = :#{#orderDto.customerName}"
    		+ " and o.customerPhone = :#{#orderDto.customerPhone}")
	Optional<Order> getOrderByCustomerNameAndCustomerPhone(@Param("orderDto") OrderDto orderDto);

    @Query(value = "select o from Order o " +
            "where o.orderDate >= TO_DATE(:#{#orderDto.fromDate}, 'yyyy-MM-dd')"
    		+ " and o.orderDate <= TO_DATE(:#{#orderDto.toDate}, 'yyyy-MM-dd')"
    		+ " and o.customerName like '%' || :#{#orderDto.customerName} || '%'"
    )
	Optional<List<Order>> getOrdersFromDateToToDate(@Param("orderDto") OrderDto orderDto);

    
    @Transactional
    @Modifying
    @Query(value = "update Order o set "
    		+ "o.totalPrice = :#{#updateOrder.totalPrice}, "
    		+ "o.paymentMethod = :#{#updateOrder.paymentMethod}, "
    		+ "o.isPaid = :#{#updateOrder.isPaid} "
    		+ "where o.id = :id"
    		)
	void updateOrderById(@Param("id") Long id,@Param("updateOrder") Order updateOrder);
}
