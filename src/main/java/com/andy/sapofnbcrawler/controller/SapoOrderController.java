package com.andy.sapofnbcrawler.controller;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.request.MemberOrderRequest;
import com.andy.sapofnbcrawler.response.CommonResponse;
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
    	Object cartOrder = new Object();
    	CommonResponse commonResponse = new CommonResponse();
    	try {
    		cartOrder = orderService.getCartOrder();
    		commonResponse.setData(cartOrder);
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_SUCCESS);
    		commonResponse.setMessage("Cart order get successfully.");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Cart order get failed with error: " + e.getMessage());
    		return ResponseEntity.internalServerError().body(commonResponse);
		}
    }
    
    @PostMapping("")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<?> placeOrder(@RequestBody MemberOrderRequest request) {
    	Object order = new Object();
    	CommonResponse commonResponse = new CommonResponse();
    	try {
    		order = orderService.placeOrder(request);
    		commonResponse.setData(order);
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_SUCCESS);
    		commonResponse.setMessage("Place order successfully.");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Place order failed with error: " + e.getMessage());
    		return ResponseEntity.internalServerError().body(commonResponse);
		}
    }
    
    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> editOrder(@PathVariable String id, @RequestBody MemberOrderRequest request) {
    	Object order = new Object();
    	CommonResponse commonResponse = new CommonResponse();
    	try {
    		order = orderService.editOrder(id, request);
    		commonResponse.setData(order);
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_SUCCESS);
    		commonResponse.setMessage("Edit order successfully.");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Edit order failed with error: " + e.getMessage());
    		return ResponseEntity.internalServerError().body(commonResponse);
		}
    }
    
    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
    	Object order = new Object();
    	CommonResponse commonResponse = new CommonResponse();
    	try {
    		order = orderService.getOrderById(id);
    		commonResponse.setData(order);
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_SUCCESS);
    		commonResponse.setMessage("Get order successfully.");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Get order failed with error: " + e.getMessage());
    		return ResponseEntity.internalServerError().body(commonResponse);
		}
    }
    
   @DeleteMapping("/{id}")
   public ResponseEntity<?> deleteOrder(@PathVariable String id) {
	   	Object order = new Object();
	   	CommonResponse commonResponse = new CommonResponse();
	   	try {
	   		order = orderService.deleteOrder(id);
	   		commonResponse.setData(order);
	   		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_SUCCESS);
	   		commonResponse.setMessage("Delete order successfully.");
	   		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
	   		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
	   		commonResponse.setMessage("Delete order failed with error: " + e.getMessage());
	   		return ResponseEntity.internalServerError().body(commonResponse);
		}
   }
}
