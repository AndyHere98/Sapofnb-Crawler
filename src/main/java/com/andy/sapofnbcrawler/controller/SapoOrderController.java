package com.andy.sapofnbcrawler.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andy.sapofnbcrawler.dto.ErrorResponseDto;
import com.andy.sapofnbcrawler.dto.MemberOrderDto;
import com.andy.sapofnbcrawler.dto.OrderDto;
import com.andy.sapofnbcrawler.dto.ResponseDto;
import com.andy.sapofnbcrawler.service.IOrderService;
import com.andy.sapofnbcrawler.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Thông tin đơn hàng REST API", description = "Thông tin về các đơn hàng hiển thị dạng REST API")
@RestController
@RequestMapping(path = "${sapo-api.version}/order", produces = { MediaType.APPLICATION_JSON_VALUE })
@AllArgsConstructor
public class SapoOrderController {

	private IOrderService orderService;

	@Operation(summary = "Lấy danh sách đơn hàng hôm nay")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công", content = @Content(schema = @Schema(implementation = OrderDto.class))),
//    	@ApiResponse(
//    		responseCode = "417", description = "Thông tin đơn hàng nhận về không như dự kiến, hãy liên lạc dev để check kỹ hơn",
//			content = @Content(
//				schema = @Schema(implementation = CommonResponse.class)
//			)
//		),
		@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/cart")
	public ResponseEntity<OrderDto> getCartOrder() {
		OrderDto cartOrder = new OrderDto();
		cartOrder = orderService.checkTodayOrder();
		return ResponseEntity.ok(cartOrder);
	}
	
	@Operation(summary = "Lấy danh sách đơn hàng")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công", content = @Content(schema = @Schema(implementation = OrderDto.class))),
		@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/search")
	public ResponseEntity<List<OrderDto>> getOrdersWithCondition(@RequestParam("customerName") String customerName, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate) {
		List<OrderDto> allOrder = new ArrayList<>();
		
		allOrder = orderService.getOrdersWithCondition(customerName, fromDate, toDate);
		return ResponseEntity.ok(allOrder);
	}
	
	@Operation(summary = "Lấy danh sách đơn hàng")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công", content = @Content(schema = @Schema(implementation = OrderDto.class))),
		@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/all")
	public ResponseEntity<List<OrderDto>> getAllOrders() {
		List<OrderDto> allOrder = new ArrayList<>();
		
		allOrder = orderService.getAllOrders();
		return ResponseEntity.ok(allOrder);
	}

	@Operation(summary = "Đặt đơn hàng")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Yêu cầu được thực hiện thành công", content = @Content(schema = @Schema(implementation = OrderDto.class))),
		@ApiResponse(responseCode = "417", description = "Thông tin đơn hàng nhận về không như dự kiến, hãy liên lạc dev để check kỹ hơn", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
		@ApiResponse(responseCode = "500", description = "Lấy thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping("/place")
	public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderDto request) {
		boolean isCreated = orderService.placeOrder(request);
		ResponseDto commonResponse = new ResponseDto();

		if (isCreated) {
			commonResponse.setStatus(HttpStatus.CREATED);
			commonResponse.setMessage("Đặt đơn hàng thành công");
			return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
		} else {
			commonResponse.setStatus(HttpStatus.EXPECTATION_FAILED);
			commonResponse.setMessage("Đơn đặt hàng của bạn bị lỗi. Xin hãy check lại với Admin");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(commonResponse);
		}
	}

	@Operation(summary = "Chỉnh sửa thông tin đơn hàng")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
		@ApiResponse(responseCode = "417", description = "Thông tin đơn hàng xử lý không như dự kiến, hãy liên lạc dev để check kỹ hơn", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
		@ApiResponse(responseCode = "500", description = "Xử lý cập nhật thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PutMapping("/{orderSku}")
	public ResponseEntity<ResponseDto> editOrder(
			@NotBlank(message = "Mã đơn hàng không thể trống") @Size(max = 36) @PathVariable("orderSku") String orderSku,
			@Valid @RequestBody OrderDto request) {
		ResponseDto commonResponse = new ResponseDto();

		boolean isUpdated = orderService.editOrder(orderSku, request);

		if (isUpdated) {
			commonResponse.setStatus(HttpStatus.OK);
			commonResponse.setMessage("Chỉnh sửa đơn hàng thành công");
			return ResponseEntity.ok(commonResponse);
		} else {
			commonResponse.setStatus(HttpStatus.EXPECTATION_FAILED);
			commonResponse.setMessage("Đơn hàng chỉnh sửa thất bại. Hãy check lại với Admin");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(commonResponse);
		}
	}

	@Operation(summary = "Lấy thông tin đơn hàng bằng mã đơn hàng")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công", content = @Content(schema = @Schema(implementation = MemberOrderDto.class))),
		@ApiResponse(responseCode = "404", description = "Lấy thông tin đơn hàng không thành công, kiểm tra lỗi và liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{orderSku}")
	public ResponseEntity<OrderDto> getOrderByOrderCode(
			@NotBlank(message = "Mã đơn hàng không thể trống") @Size(max = 36) @PathVariable("orderSku") String orderSku) {
		OrderDto order = orderService.getOrderById(orderSku);
		return ResponseEntity.ok(order);
	}

	@Operation(summary = "Xoá thông tin đơn hàng")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Yêu cầu được thực hiện thành công", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
		@ApiResponse(responseCode = "417", description = "Thông tin đơn hàng xử lý không như dự kiến, hãy liên lạc dev để check kỹ hơn", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
		@ApiResponse(responseCode = "500", description = "Xử lý xoá thông tin đơn hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@DeleteMapping("/{orderSku}")
	public ResponseEntity<ResponseDto> deleteOrder(
			@NotBlank(message = "Mã đơn hàng không thể trống") @Size(max = 36) @PathVariable("orderSku") String orderSku) {
		ResponseDto commonResponse = new ResponseDto();

		boolean isDeleted = orderService.deleteOrder(orderSku);

		if (isDeleted) {
			commonResponse.setStatus(HttpStatus.OK);
			commonResponse.setMessage("Bạn đã xoá đơn hàng thành công");
			return ResponseEntity.ok(commonResponse);
		} else {
			commonResponse.setStatus(HttpStatus.EXPECTATION_FAILED);
			commonResponse.setMessage("Đơn hàng xoá không thành công. Hãy check lại với Admin");
			return ResponseEntity.internalServerError().body(commonResponse);
		}
	}
}
