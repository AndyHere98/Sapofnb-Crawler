package com.andy.sapofnbcrawler.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andy.sapofnbcrawler.request.SummaryRequest;
import com.andy.sapofnbcrawler.response.ErrorResponse;
import com.andy.sapofnbcrawler.response.OrderResponse;
import com.andy.sapofnbcrawler.response.SummaryResponse;
import com.andy.sapofnbcrawler.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(
	name = "Trang Admin quản lý thông tin với REST API",
	description = "Trang Admin quản lý thông tin hiển thị dạng REST API"
)
@RestController
@RequestMapping(path = "${sapo-api.version}/admin", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class SapoAdminController {
    
    private final AdminService adminService;
    
    @Operation(
    	summary = "Tổng hợp danh sách đặt hàng hôm nay"	
    )
    @ApiResponses({
    	@ApiResponse(
			responseCode = "200", description = "Yêu cầu được thực hiện thành công",
			content = @Content(
				schema = @Schema(implementation = OrderResponse.class)
			)
		),
    	@ApiResponse(
    		responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev",
    		content = @Content(
				schema = @Schema(implementation = ErrorResponse.class)
			)
		)
    })
    @GetMapping("/today-order")
    public ResponseEntity<OrderResponse> summaryTodayOrder() {
    	OrderResponse order = adminService.summaryTodayOrder();
		return ResponseEntity.ok(order);
    }
    
    @Operation(
    	summary = "Tổng hợp danh sách đặt hàng theo từng member"	
    )
    @ApiResponses({
    	@ApiResponse(
			responseCode = "200", description = "Yêu cầu được thực hiện thành công"
		),
    	@ApiResponse(
    		responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev",
    		content = @Content(
				schema = @Schema(implementation = ErrorResponse.class)
			)
		)
    })
    @GetMapping("/member-today-order")
    public ResponseEntity<List<OrderResponse>> summaryTodayOrderByMember() {
    	List<OrderResponse> orderList = adminService.summaryTodayOrderByMember();
		return ResponseEntity.ok(orderList);
    }
    
    
    @Operation(
        	summary = "Tổng hợp danh sách đơn hàng theo thời gian"	
        )
        @ApiResponses({
        	@ApiResponse(
    			responseCode = "200", description = "Yêu cầu được thực hiện thành công"
    		),
        	@ApiResponse(
        		responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev",
        		content = @Content(
    				schema = @Schema(implementation = ErrorResponse.class)
    			)
    		)
        })
    @GetMapping("/summary")
    public ResponseEntity<List<SummaryResponse>> summaryOrdersByTime(@Valid @RequestBody SummaryRequest request) {
    	List<SummaryResponse> orderList = adminService.summaryOrdersByTime(request);
		return ResponseEntity.ok(orderList);
    }
}
