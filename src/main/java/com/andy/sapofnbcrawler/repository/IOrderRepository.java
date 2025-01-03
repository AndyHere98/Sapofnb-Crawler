package com.andy.sapofnbcrawler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.request.MemberOrderRequest;

@Repository
public interface IOrderRepository extends JpaRepository<Order, String> {
    
//    @Query(value = "select o from Order o " +
//                   "where FORMATDATETIME(o.orderDate, 'dd/MM/yyyy') = FORMATDATETIME(PARSEDATETIME(:today, 'dd/MM/yyyy'), 'dd/MM/yyyy')")
    @Query(value = "select o from Order o " +
            "where TO_CHAR(o.orderDate, 'dd/MM/yyyy') = TO_CHAR(TO_DATE(:today, 'dd/MM/yyyy'), 'dd/MM/yyyy')")
    List<Order> getOrderByOrderDate(@Param("today") String today);
    
//    @Query(value = "select o from Order o " +
//                   "where FORMATDATETIME(o.orderDate, 'dd/MM/yyyy') = FORMATDATETIME(PARSEDATETIME(:today, 'dd/MM/yyyy'), 'dd/MM/yyyy')" +
//                   "order by o.customerName")
    @Query(value = "select o from Order o " +
            "where TO_CHAR(o.orderDate, 'dd/MM/yyyy') = TO_CHAR(TO_DATE(:today, 'dd/MM/yyyy'), 'dd/MM/yyyy')" +
            "order by o.customerName")
    List<Order> getOrdersByOrderDateOrderByCustomerName(@Param("today") String today);
    
//    @Query(value = "select o from Order o where o.customerName = :#{#request.customerName}" +
//                   " and FORMATDATETIME(o.orderDate, 'dd/MM/yyyy') = FORMATDATETIME(current_date, 'dd/MM/yyyy')"
//                   )
    @Query(value = "select o from Order o where o.customerName = :#{#request.customerName}" +
            " and TO_CHAR(o.orderDate, 'dd/MM/yyyy') = TO_CHAR(current_date, 'dd/MM/yyyy')"
            )
    Optional<Order> getOrderByOrderDateOrderByCustomerName(@Param("request") MemberOrderRequest request);
    
//    @Query(value = "select o from Order o where FORMATDATETIME(o.orderDate, 'dd/MM/yyyy') >= FORMATDATETIME(PARSEDATETIME(:fromDate, 'dd/MM/yyyy'), 'dd/MM/yyyy')"
//    		+ " and o.customerName like '%' || :customerName || '%'"
//    		+ " order by o.orderDate asc, o.customerName asc")
    @Query(value = "select o from Order o where TO_CHAR(o.orderDate, 'dd/MM/yyyy') >= TO_CHAR(TO_DATE(:fromDate, 'dd/MM/yyyy'), 'dd/MM/yyyy')"
    		+ " and o.customerName like '%' || :customerName || '%'"
    		+ " order by o.orderDate asc, o.customerName asc")
    List<Order> getOrderByOrderDateAndCustomerNameOrderByOrderDateAsc(@Param("customerName") String customerName,@Param("fromDate") String fromDate);
}
