package com.andy.sapofnbcrawler.controller;

import com.andy.sapofnbcrawler.request.MemberOrderRequest;
import com.andy.sapofnbcrawler.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${sapo-api.version}/order")
@RequiredArgsConstructor
public class SapoOrderController {
    
    private final OrderService orderService;

    @GetMapping("/cart")
    public ResponseEntity<?> getCartOrder() {
        return ResponseEntity.ok(orderService.getCartOrder());
    }
    
    @PostMapping("")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?> placeOrder(@RequestBody MemberOrderRequest request) {
    	
    	return ResponseEntity.ok(orderService.placeOrder(request));
    }
}
