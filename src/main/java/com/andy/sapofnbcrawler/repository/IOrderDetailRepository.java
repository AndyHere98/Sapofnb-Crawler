package com.andy.sapofnbcrawler.repository;

import com.andy.sapofnbcrawler.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail, Long> {

}
