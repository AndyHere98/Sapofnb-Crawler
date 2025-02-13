package com.andy.sapofnbcrawler.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.entity.CustomerInfo;
import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.object.BillingSummary;
import com.andy.sapofnbcrawler.object.CustomerRank;
import com.andy.sapofnbcrawler.object.DailySummaryOrders;
import com.andy.sapofnbcrawler.object.OrderSummary;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {

        // @Query(value = "select o from Order o " +
        // "where FORMATDATETIME(o.orderDate, 'yyyy-MM-dd') =
        // FORMATDATETIME(PARSEDATETIME(:today, 'yyyy-MM-dd'), 'yyyy-MM-dd')")
        @Query(value = "select o from Order o "
                        + "where o.orderDate = :orderDate")
        Optional<List<Order>> getOrderByOrderDate(@Param("orderDate") java.sql.Date orderDate);

        // @Query(value = "select o from Order o " +
        // "where FORMATDATETIME(o.orderDate, 'yyyy-MM-dd') =
        // FORMATDATETIME(PARSEDATETIME(:today, 'yyyy-MM-dd'), 'yyyy-MM-dd')" +
        // "order by o.customerName")
        @Query(value = "select o from Order o " +
                        "where TO_CHAR(o.orderDate, 'yyyy-MM-dd') = TO_CHAR(TO_DATE(:today, 'yyyy-MM-dd'), 'yyyy-MM-dd')"
        // + "order by o.customerName"
        )
        Optional<List<Order>> getOrdersByOrderDateOrderByCustomerName(@Param("today") String today);

        // @Query(value = "select o from Order o where o.customerName =
        // :#{#request.customerName}" +
        // " and FORMATDATETIME(o.orderDate, 'yyyy-MM-dd') =
        // FORMATDATETIME(current_date, 'yyyy-MM-dd')"
        // )
        // @Query(value = "select o from Order o where o.customerName =
        // :#{#request.customerName}" +
        // " and TO_CHAR(o.orderDate, 'yyyy-MM-dd') = TO_CHAR(current_date,
        // 'yyyy-MM-dd')"
        // )
        // Optional<Order> getOrderByOrderDateOrderByCustomerName(@Param("request")
        // MemberOrderRequest request);

        // @Query(value = "select o from Order o where FORMATDATETIME(o.orderDate,
        // 'yyyy-MM-dd') >= FORMATDATETIME(PARSEDATETIME(:fromDate, 'yyyy-MM-dd'),
        // 'yyyy-MM-dd')"
        // + " and o.customerName like '%' || :customerName || '%'"
        // + " order by o.orderDate asc, o.customerName asc")
        // @Query(value = "select o from Order o where TO_CHAR(o.orderDate,
        // 'yyyy-MM-dd') >= TO_CHAR(TO_DATE(:fromDate, 'yyyy-MM-dd'), 'yyyy-MM-dd')"
        //// + " and o.customerName like '%' || :customerName || '%'"
        // + " order by o.orderDate asc")
        // Optional<List<Order>>
        // getOrderByOrderDateAndCustomerNameOrderByOrderDateAsc(@Param("customerName")
        // String customerName,@Param("fromDate") String fromDate);

        @Query(value = "select o from Order o where o.orderSku = :orderSku")
        Optional<Order> findByOrderCode(@Param("orderSku") String orderSku);

        @Query(value = "select o from Order o " +
                        "where TO_CHAR(o.orderDate, 'yyyy-MM-dd') = TO_CHAR(TO_DATE(:#{#orderDto.orderDate}, 'yyyy-MM-dd'), 'yyyy-MM-dd')"
        // + " and o.customerName = :#{#orderDto.customerName}"
        // + " and o.customerPhone = :#{#orderDto.customerPhone}"
        )
        Optional<Order> getOrderByCustomerNameAndCustomerPhone(@Param("orderDto") OrderDto orderDto);

        @Query(value = "select o from Order o " +
                        "where o.orderDate >= TO_DATE(:#{#orderDto.fromDate}, 'yyyy-MM-dd')"
                        + " and o.orderDate <= TO_DATE(:#{#orderDto.toDate}, 'yyyy-MM-dd')"
        // + " and o.customerName like '%' || :#{#orderDto.customerName} || '%'"
        )
        Optional<List<Order>> getOrdersFromDateToToDate(@Param("orderDto") OrderDto orderDto);

        @Transactional
        @Modifying
        @Query(value = "update Order o set "
                        + "o.totalPrice = :#{#updateOrder.totalPrice}, "
                        + "o.paymentMethod = :#{#updateOrder.paymentMethod}, "
                        + "o.isPaid = :#{#updateOrder.isPaid} "
                        + "where o.id = :id")
        void updateOrderById(@Param("id") Long id, @Param("updateOrder") Order updateOrder);

        @Query(value = "select distinct to_char(o.orderDate, 'yyyy') as year from Order o"
                        + " order by year")
        List<String> getDistinctYearOrder();

        @Query(value = "select o from Order o"
                        + " where to_char(o.orderDate, 'yyyy') = :year"
                        + " order by o.orderDate")
        List<Order> getOrdersInYear(@Param("year") String year);

        @Query(name = "getRankingCustomer", nativeQuery = true)
        List<CustomerRank> rankCustomerInMonthAndYear(@Param("start") Date start, @Param("end") Date end);

        @Query(name = "summaryDailyOrders", nativeQuery = true)
        List<DailySummaryOrders> summaryDailyOrders();

        @Query(name = "getRankingCustomerAllTime", nativeQuery = true)
        List<CustomerRank> rankCustomerAllTime();

        @Query(value = "select sum(o.totalPrice) as totalDebt from Order o"
                        + " where o.customerId = :customer"
                        + " and o.isPaid = 0"
                        + " and o.orderStatus = 'P'")
        BigDecimal getTotalDebtOfCustomer(@Param("customer") CustomerInfo customer);

        @Query(value = "select o from Order o"
                        + " where o.isPaid = 0"
                        + " and o.orderStatus = 'P'")
        List<Order> getUnpaidOrder();

        @Query(name = "summaryBilling", nativeQuery = true)
        BillingSummary summaryBilling(@Param("date") Date date);

        @Query(name = "summaryOrders", nativeQuery = true)
        OrderSummary summaryOrders();

        @Modifying
        @Transactional
        @Query(value = "update Order o set "
                        + "o.orderStatus = :status "
                        + "where o.id = :id")
        void updateOrderStatus(@Param("id") Long id, @Param("status") String status);

        @Modifying
        @Transactional
        @Query(value = "update Order o set "
                        + "o.isPaid = 1 "
                        + "where o.id = :id")
        void confirmPaymentOrder(@Param("id") Long id);

        @Modifying
        @Transactional
        @Query(value = "update Order o set "
                        + "o.orderStatus = 'S' "
                        + "where o.id = :id")
        void completeOrder(@Param("id") Long id);
}
