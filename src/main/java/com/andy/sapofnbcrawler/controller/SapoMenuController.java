package com.andy.sapofnbcrawler.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.response.CommonResponse;
import com.andy.sapofnbcrawler.response.MenuResponse;
import com.andy.sapofnbcrawler.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${sapo-api.version}")
@RequiredArgsConstructor
public class SapoMenuController {
    
    private final MenuService menuService;
    
    @GetMapping("/get-menu")
    public ResponseEntity<?> getMenu() {
    	MenuResponse menuResponse = new MenuResponse();
    	CommonResponse commonResponse = new CommonResponse();
    	try {
    		menuResponse = menuService.getMenu();
    		commonResponse.setData(menuResponse);
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_SUCCESS);
    		commonResponse.setMessage("Today menu get successfully.");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Today menu get failed with error: " + e.getMessage());
    		return ResponseEntity.internalServerError().body(commonResponse);
		}
    	
    }
}
