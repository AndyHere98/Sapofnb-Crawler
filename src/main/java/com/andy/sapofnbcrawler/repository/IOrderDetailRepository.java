package com.andy.sapofnbcrawler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.andy.sapofnbcrawler.entity.Order;
import com.andy.sapofnbcrawler.entity.OrderDetail;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    
    List<OrderDetail> findAllByOrder(Order order);
    
    @Modifying
    @Query(value = "Delete from OrderDetail o where o.order = :order")
    void deleteOrderDetailByOrder(@Param("order") Order order);
}
