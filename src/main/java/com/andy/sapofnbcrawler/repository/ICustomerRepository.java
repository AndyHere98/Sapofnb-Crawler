package com.andy.sapofnbcrawler.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.andy.sapofnbcrawler.dto.CustomerInfoDto;
import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.entity.CustomerInfo;

@Repository
public interface ICustomerRepository extends JpaRepository<CustomerInfo, Long> {

	@Query(value = "select c from CustomerInfo c where c.ipAddress = :ipAddress")
	Optional<CustomerInfo> findCustomerByIpAddress(@Param("ipAddress") String ipAddress);

	@Modifying
	@Transactional
	@Query(value = "update CustomerInfo c set "
			+ " c.customerName = :#{#customerInfoDto.customerName}"
			+ ", c.customerPhone = :#{#customerInfoDto.customerPhone}"
			+ ", c.customerEmail = :#{#customerInfoDto.customerEmail}"
			+ ", c.updatedDate = SYSTIMESTAMP"
			+ ", c.updatedBy = :#{#adminInfo.customerName}"
			+ " where c.ipAddress = :ipAddress")
	void updateCustomerByIpAddress(@Param("ipAddress") String ipAddress,
			@Param("customerInfoDto") CustomerInfoDto customerInfoDto, @Param("adminInfo") CustomerInfo adminInfo);

	@Query(value = "Select c from CustomerInfo c "
			+ " where c.customerName = :#{#orderDto.customerName}"
			+ " and c.customerPhone = :#{#orderDto.customerPhone}"
			+ " and c.customerEmail = :#{#orderDto.customerEmail}")
	Optional<CustomerInfo> findCustomerByNameAndPhoneAndEmail(@Param("orderDto") OrderDto orderDto);

	@Query(value = "Select c from CustomerInfo c "
			+ " where c.customerName = :#{#customerInfoDto.customerName}"
			+ " and c.customerPhone = :#{#customerInfoDto.customerPhone}"
			+ " and c.customerEmail = :#{#customerInfoDto.customerEmail}")
	Optional<CustomerInfo> findCustomerByCustomerInformation(@Param("customerInfoDto") CustomerInfoDto customerInfoDto);
	
	@Modifying
	@Transactional
	@Query(value = "update CustomerInfo c set "
			+ " c.ipAddress = :#{#customerInfoDto.ipAddress}"
			+ ", c.pcHostName = :#{#customerInfoDto.pcHostName}"
			+ ", c.updatedDate = SYSTIMESTAMP"
			+ ", c.updatedBy = :#{#customerInfoDto.pcHostName}"
			+ " where c.id = :id")
	void updateCustomerInfoById(@Param("customerInfoDto") CustomerInfoDto customerInfoDto, @Param("id") Long id);

}
