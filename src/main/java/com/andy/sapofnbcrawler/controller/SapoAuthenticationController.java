package com.andy.sapofnbcrawler.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andy.sapofnbcrawler.dto.CustomerInfoDto;
import com.andy.sapofnbcrawler.dto.ErrorResponseDto;
import com.andy.sapofnbcrawler.dto.ResponseDto;
import com.andy.sapofnbcrawler.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(
	name = "Thông tin Menu với REST API",
	description = "Thông tin về các món ăn hiển thị dạng REST API"
)
@RestController
@RequestMapping(path = "${sapo-api.version}/user", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class SapoAuthenticationController {
	
	private final AuthenticationService authenticationService;
	private final HttpServletRequest request;
    
    @GetMapping("/auth")
    public ResponseEntity<CustomerInfoDto> getCustomer(HttpServletRequest request) {
    	 Map<String, String> response = new HashMap<>();
    	 CustomerInfoDto customerInfoDto = new CustomerInfoDto();

         try {
             // Get remote IP address from request or use provided IP
             String remoteIp = request.getRemoteAddr();

             // Perform reverse DNS lookup
             InetAddress inetAddress = InetAddress.getByName(remoteIp);
             String hostname = inetAddress.getHostName();

             response.put("ipAddress: ", remoteIp);
             response.put("hostname: ", hostname);
             
             System.out.println("remoteIp: " + remoteIp);
             System.out.println("hostname: " + hostname);
             System.out.println("port: " + request.getRemotePort());
             

             customerInfoDto = authenticationService.getCustomer(remoteIp);
             
         } catch (UnknownHostException e) {
             response.put("error", "Unable to resolve hostname for the given IP.");
         }
         
         return ResponseEntity.ok(customerInfoDto);
    }
    
    
    
    @Operation(summary = "Đăng ký thông tin khách hàng")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Yêu cầu được thực hiện thành công", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
		@ApiResponse(responseCode = "417", description = "Thông tin khách hàng nhận về không như dự kiến, hãy liên lạc dev để check kỹ hơn", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
		@ApiResponse(responseCode = "500", description = "Lấy thông tin khách hàng không thành công, liên hệ với dev", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(@RequestBody CustomerInfoDto customerInfoDto) {
		ResponseDto commonResponse = new ResponseDto();
        String remoteIp = request.getRemoteAddr();
        
        customerInfoDto.setIpAddress(remoteIp);
        customerInfoDto.setPcHostName(request.getRemoteHost());
        boolean isCreated = authenticationService.registerUser(customerInfoDto);

		if (isCreated) {
			commonResponse.setStatus(HttpStatus.CREATED);
			commonResponse.setMessage("Đăng ký khách hàng thành công");
			return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
		} else {
			commonResponse.setStatus(HttpStatus.EXPECTATION_FAILED);
			commonResponse.setMessage("Đăng ký thông tin khách hàng của bạn bị lỗi. Xin hãy check lại với Admin");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(commonResponse);
		}
    }
}
