package com.andy.sapofnbcrawler.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.andy.sapofnbcrawler.entity.CustomerInfo;
import com.andy.sapofnbcrawler.repository.ICustomerRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component("auditAware")
@RequiredArgsConstructor
public class AuditAware implements AuditorAware<String>{
	
	private final ICustomerRepository customerRepository;
	private final HttpServletRequest request;

	@Override
	public Optional<String> getCurrentAuditor() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		SecurityContext context = SecurityContextHolder.getContext();
		CustomerInfo customer = customerRepository.findCustomerByIpAddress(request.getRemoteAddr())
				.orElseThrow(() -> new RuntimeException("Customer is not registered with ip " + request.getRemoteAddr()));
		
		return Optional.of(customer.getCustomerName());
	}
}
