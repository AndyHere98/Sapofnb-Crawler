package com.andy.sapofnbcrawler.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.andy.sapofnbcrawler.entity.CustomerInfo;

@Repository
public interface ICustomerRepository extends JpaRepository<CustomerInfo, Long> {
    
	@Query(value = "select c from CustomerInfo c where c.ipAddress = :ipAddress")
    Optional<CustomerInfo> findCustomerByIpAddress(@Param("ipAddress") String ipAddress);

}
