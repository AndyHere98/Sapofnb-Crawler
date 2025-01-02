package com.andy.sapofnbcrawler.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.request.SummaryRequest;
import com.andy.sapofnbcrawler.response.CommonResponse;
import com.andy.sapofnbcrawler.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${sapo-api.version}/admin")
@RequiredArgsConstructor
public class SapoAdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/today-order")
    public ResponseEntity<?> summaryTodayOrder() {
    	Object order = new Object();
    	CommonResponse commonResponse = new CommonResponse();
    	try {
    		order = adminService.summaryTodayOrder();
    		commonResponse.setData(order);
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_SUCCESS);
    		commonResponse.setMessage("Summary order successfully.");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Summary order failed with error: " + e.getMessage());
    		return ResponseEntity.internalServerError().body(commonResponse);
		}
//        return ResponseEntity.ok(adminService.summaryTodayOrder());
    }
    
    @GetMapping("/member-today-order")
    public ResponseEntity<?> summaryTodayOrderByMember() {
    	Object order = new Object();
    	CommonResponse commonResponse = new CommonResponse();
    	try {
    		order = adminService.summaryTodayOrderByMember();
    		commonResponse.setData(order);
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_SUCCESS);
    		commonResponse.setMessage("Summary order successfully.");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Summary order failed with error: " + e.getMessage());
    		return ResponseEntity.internalServerError().body(commonResponse);
		}
//        return ResponseEntity.ok(adminService.summaryTodayOrderByMember());
    }
    
    @GetMapping("/summary")
    public ResponseEntity<?> summaryOrdersByTime(@RequestBody SummaryRequest request) {
    	Object order = new Object();
    	CommonResponse commonResponse = new CommonResponse();
    	try {
    		order = adminService.summaryOrdersByTime(request);
    		commonResponse.setData(order);
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_SUCCESS);
    		commonResponse.setMessage("Summary order successfully.");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Summary order failed with error: " + e.getMessage());
    		return ResponseEntity.internalServerError().body(commonResponse);
		}
//        return ResponseEntity.ok(adminService.summaryOrdersByTime(request));
    }
}
