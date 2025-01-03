package com.andy.sapofnbcrawler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.andy.sapofnbcrawler.common.SapoConstants;
import com.andy.sapofnbcrawler.request.MemberOrderRequest;
import com.andy.sapofnbcrawler.response.CommonResponse;
import com.andy.sapofnbcrawler.service.OrderService;

import lombok.RequiredArgsConstructor;

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
    		commonResponse.setMessage("Thông tin giỏ hàng được truy xuất thành công");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Truy xuất thông tin giỏ hàng đang lỗi: " + e.getMessage());
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
    		commonResponse.setMessage("Bạn đặt đơn hàng hoàn tất.");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Đơn đặt hàng của bạn bị lỗi vì: " + e.getMessage());
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
    		commonResponse.setMessage("Chỉnh sửa đơn hàng thành công");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Đơn hàng không thể chỉnh sửa vì: " + e.getMessage());
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
    		commonResponse.setMessage("Lấy thông tin đơn hoàn tất");
    		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
    		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
    		commonResponse.setMessage("Lấy thông tin đơn hàng đang xảy ra lỗi: " + e.getMessage());
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
	   		commonResponse.setMessage("Bạn đã xoá đơn hàng thành công");
	   		return ResponseEntity.ok(commonResponse);
		} catch (Exception e) {
	   		commonResponse.setStatus(SapoConstants.RESPONSE_STATUS_FAILED);
	   		commonResponse.setMessage("Đơn hàng không thể xoá vì: " + e.getMessage());
	   		return ResponseEntity.internalServerError().body(commonResponse);
		}
   }
}
