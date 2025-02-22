package com.andy.sapofnbcrawler.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.andy.sapofnbcrawler.entity.CustomerInfo;
import com.andy.sapofnbcrawler.repository.ICustomerRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component("auditAware")
@RequiredArgsConstructor
public class AuditAware implements AuditorAware<String> {

	private final ICustomerRepository customerRepository;
	private final HttpServletRequest request;

	@Override
	public @NonNull Optional<String> getCurrentAuditor() {
		CustomerInfo customer = customerRepository.findCustomerByIpAddress(request.getRemoteAddr())
				.orElseThrow(() -> new RuntimeException("Khách hàng chưa đăng ký tài khoản với ip " + request.getRemoteAddr()
						+ " và hostname: " + request.getRemoteHost()));
		return Optional.of(customer.getCustomerName());
	}
}
