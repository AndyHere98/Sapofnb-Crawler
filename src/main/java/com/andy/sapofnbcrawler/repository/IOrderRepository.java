package com.andy.sapofnbcrawler.repository;

import com.andy.sapofnbcrawler.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderRepository extends JpaRepository<Order, String> {

}
