package com.andy.sapofnbcrawler.controller.admin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andy.sapofnbcrawler.dto.AdminCustomerSummaryDto;
import com.andy.sapofnbcrawler.dto.AdminOrderSummaryDto;
import com.andy.sapofnbcrawler.dto.ErrorResponseDto;
import com.andy.sapofnbcrawler.dto.MemberOrderDto;
import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Tag(name = "Trang Admin quản lý thông tin với REST API", description = "Trang Admin quản lý thông tin hiển thị dạng REST API")
@RestController
@RequestMapping(path = "${sapo-api.version}/admin", produces = { MediaType.APPLICATION_JSON_VALUE })
@RequiredArgsConstructor
@Validated
public class SapoAdminController {

	private final AdminService adminService;


	@Operation(summary = "Tổng hợp danh sách đặt hàng theo từng member")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công"),
			@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
	})
	@GetMapping("/orders/summary")
	public ResponseEntity<AdminOrderSummaryDto> summaryOrder() {
		AdminOrderSummaryDto order = adminService.summaryOrders();
		return ResponseEntity.ok(order);
	}

	@Operation(summary = "Tổng hợp danh sách đặt hàng theo từng member")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công"),
			@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
	})
	@GetMapping("/customers/summary")
	public ResponseEntity<AdminCustomerSummaryDto> summaryCustomers() {
		AdminCustomerSummaryDto customerSummaryDto = adminService.summaryCustomers();
		return ResponseEntity.ok(customerSummaryDto);
	}

//	@Operation(summary = "Tổng hợp danh sách đơn hàng theo thời gian")
//	@ApiResponses({
//			@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công"),
//			@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
//	})
//	@GetMapping("/summary")
//	public ResponseEntity<List<MemberOrderDto>> summaryTodayOrderByMember(@RequestBody OrderDto orderDto) {
//		List<MemberOrderDto> orderList = adminService.summaryTodayOrderByMember(orderDto);
//		return ResponseEntity.ok(orderList);
//	}
}
