package com.andy.sapofnbcrawler.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.andy.sapofnbcrawler.dto.CustomerInfoDto;
import com.andy.sapofnbcrawler.entity.CustomerInfo;
import com.andy.sapofnbcrawler.repository.ICustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final ICustomerRepository customerRepository;

	public boolean registerUser(CustomerInfoDto customerInfoDto) {

		Optional<CustomerInfo> customerInfo = customerRepository
				.findCustomerByIpAddress(customerInfoDto.getIpAddress());
		if (customerInfo.isPresent()) {
			throw new RuntimeException(String.format("There is existed user with this ip %s. Please contact admin",
					customerInfoDto.getIpAddress()));
		}
		CustomerInfo customer = mapToCustomerInfo(customerInfoDto);
		customer.setCreatedBy(customer.getPcHostName());
		customer.setCreatedDate(LocalDateTime.now());
		customerRepository.save(customer);

		return true;
	}

	public CustomerInfoDto getCustomer(String ipAddress) {
		CustomerInfo customer = customerRepository.findCustomerByIpAddress(ipAddress)
				.orElseThrow(() -> new RuntimeException("Customer is not registered with ip " + ipAddress));

		return mapToCustomerInfoDto(customer);
	}

	private CustomerInfoDto mapToCustomerInfoDto(CustomerInfo customerInfo) {
		CustomerInfoDto customerInfoDto = new CustomerInfoDto();

		customerInfoDto.setIpAddress(customerInfo.getIpAddress());
		customerInfoDto.setCustomerName(customerInfo.getCustomerName());
		customerInfoDto.setCustomerPhone(customerInfo.getCustomerPhone());
		customerInfoDto.setCustomerEmail(customerInfo.getCustomerEmail());

		return customerInfoDto;
	}

	private CustomerInfo mapToCustomerInfo(CustomerInfoDto customerInfoDto) {
		CustomerInfo customerInfo = new CustomerInfo();

		customerInfo.setIpAddress(customerInfoDto.getIpAddress());
		customerInfo.setCustomerName(customerInfoDto.getCustomerName());
		customerInfo.setCustomerPhone(customerInfoDto.getCustomerPhone());
		customerInfo.setCustomerEmail(customerInfoDto.getCustomerEmail());
		customerInfo.setPcHostName(customerInfoDto.getPcHostName());

		return customerInfo;
	}
}
