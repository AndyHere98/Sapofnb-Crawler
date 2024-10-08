package com.andy.sapofnbcrawler.repository;

import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.request.MemberOrderRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<Order, String> {
    
    @Query(value = "select o from Order o " +
                   "where FORMATDATETIME(o.orderDate, 'dd/MM/yyyy') = FORMATDATETIME(PARSEDATETIME(:today, 'dd/MM/yyyy'), 'dd/MM/yyyy')")
    List<Order> getOrderByOrderDate(@Param("today") String today);
    
    @Query(value = "select o from Order o " +
                   "where FORMATDATETIME(o.orderDate, 'dd/MM/yyyy') = FORMATDATETIME(PARSEDATETIME(:today, 'dd/MM/yyyy'), 'dd/MM/yyyy')" +
                   "order by o.customerName")
    List<Order> getOrdersByOrderDateOrderByCustomerName(@Param("today") String today);
    
    @Query(value = "select o from Order o where o.customerName = :#{#request.customerName}" +
                   " and FORMATDATETIME(o.orderDate, 'dd/MM/yyyy') = FORMATDATETIME(current_date, 'dd/MM/yyyy')"
                   )
    Optional<Order> getOrderByOrderDateOrderByCustomerName(@Param("request") MemberOrderRequest request);
}
