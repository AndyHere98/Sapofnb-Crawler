package com.andy.sapofnbcrawler.controller;

import com.andy.sapofnbcrawler.request.SummaryRequest;
import com.andy.sapofnbcrawler.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${sapo-api.version}/admin")
@RequiredArgsConstructor
public class SapoAdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/today-order")
    public ResponseEntity<?> summaryTodayOrder() {
        return ResponseEntity.ok(adminService.summaryTodayOrder());
    }
    
    @GetMapping("/member-today-order")
    public ResponseEntity<?> summaryTodayOrderByMember() {
        return ResponseEntity.ok(adminService.summaryTodayOrderByMember());
    }
    
    @GetMapping("/summary")
    public ResponseEntity<?> summaryOrdersByTime(@RequestBody SummaryRequest request) {
        return ResponseEntity.ok(adminService.summaryOrdersByTime(request));
    }
}
