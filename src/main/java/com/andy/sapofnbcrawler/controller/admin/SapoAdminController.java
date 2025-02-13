package com.andy.sapofnbcrawler.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andy.sapofnbcrawler.dto.AdminBillingSummaryDto;
import com.andy.sapofnbcrawler.dto.AdminCustomerSummaryDto;
import com.andy.sapofnbcrawler.dto.AdminOrderSummaryDto;
import com.andy.sapofnbcrawler.dto.CustomerInfoDto;
import com.andy.sapofnbcrawler.dto.ErrorResponseDto;
import com.andy.sapofnbcrawler.dto.MemberOrderDto;
import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.dto.ResponseDto;
import com.andy.sapofnbcrawler.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

	@Operation(summary = "Tổng hợp danh sách member")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công"),
			@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
	})
	@GetMapping("/customers/summary")
	public ResponseEntity<AdminCustomerSummaryDto> summaryCustomers() {
		AdminCustomerSummaryDto customerSummaryDto = adminService.summaryCustomers();
		return ResponseEntity.ok(customerSummaryDto);
	}


	@Operation(summary = "Admin cập nhật thanh toán đơn hàng")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công"),
			@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
	})
	@PutMapping("/orders/confirm/{orderId}")
	public ResponseEntity<ResponseDto> confirmPaymentOrder(@NotBlank(message = "Mã đơn hàng") @Size(max = 36) @PathVariable("orderId") String orderSku) {
		ResponseDto commonResponse = new ResponseDto();

		boolean isUpdated = adminService.confirmPaymentOrder(orderSku);

		if (isUpdated) {
			commonResponse.setStatus(HttpStatus.OK);
			commonResponse.setMessage("Chỉnh sửa thông tin khách hàng thành công");
			return ResponseEntity.ok(commonResponse);
		} else {
			commonResponse.setStatus(HttpStatus.EXPECTATION_FAILED);
			commonResponse.setMessage("Thông tin khách hàng chỉnh sửa thất bại. Hãy check lại với Admin");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(commonResponse);
		}
	}


	@Operation(summary = "Admin cập nhật hoàn tất đơn hàng")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công"),
			@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
	})
	@PutMapping("/orders/complete/{orderId}")
	public ResponseEntity<ResponseDto> completeOrder(@NotBlank(message = "Mã đơn hàng") @Size(max = 36) @PathVariable("orderId") String orderSku) {
		ResponseDto commonResponse = new ResponseDto();

		boolean isUpdated = adminService.completeOrder(orderSku);

		if (isUpdated) {
			commonResponse.setStatus(HttpStatus.OK);
			commonResponse.setMessage("Chỉnh sửa thông tin khách hàng thành công");
			return ResponseEntity.ok(commonResponse);
		} else {
			commonResponse.setStatus(HttpStatus.EXPECTATION_FAILED);
			commonResponse.setMessage("Thông tin khách hàng chỉnh sửa thất bại. Hãy check lại với Admin");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(commonResponse);
		}
	}


	@Operation(summary = "Admin cập nhật thông tin member")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công"),
			@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
	})
	@PutMapping("/customers/{ipAdress}")
	public ResponseEntity<ResponseDto> updateCustomerByAdmin(@NotBlank(message = "Địa chỉ ip") @Size(max = 36) @PathVariable("ipAdress") String ipAddress,
			@Valid @RequestBody CustomerInfoDto request) {
		ResponseDto commonResponse = new ResponseDto();

		boolean isUpdated = adminService.updateCustomerByAdmin(ipAddress, request);

		if (isUpdated) {
			commonResponse.setStatus(HttpStatus.OK);
			commonResponse.setMessage("Chỉnh sửa thông tin khách hàng thành công");
			return ResponseEntity.ok(commonResponse);
		} else {
			commonResponse.setStatus(HttpStatus.EXPECTATION_FAILED);
			commonResponse.setMessage("Thông tin khách hàng chỉnh sửa thất bại. Hãy check lại với Admin");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(commonResponse);
		}
	}
	
	
	

	@Operation(summary = "Tổng hợp thông tin hoá đơn cho Admin")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công"),
			@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
	})
	@GetMapping("/billing/summary")
	public ResponseEntity<AdminBillingSummaryDto> summaryBilling() {
		AdminBillingSummaryDto orderList = adminService.summaryBilling();
		return ResponseEntity.ok(orderList);
	}
}
