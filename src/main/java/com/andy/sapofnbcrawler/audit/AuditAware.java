package com.andy.sapofnbcrawler.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.andy.sapofnbcrawler.entity.Order;

@Component("auditAware")
public class AuditAware implements AuditorAware<String>{

	@Override
	public Optional<String> getCurrentAuditor() {
		
		return Optional.of("SapoWeb-Crawler");
	}
//	public Optional<User> getCurrentAuditor() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        return Optional.ofNullable(userRepository.findByUsername(username));
//    }
}
