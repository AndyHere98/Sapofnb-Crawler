package com.andy.sapofnbcrawler.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andy.sapofnbcrawler.dto.ErrorResponseDto;
import com.andy.sapofnbcrawler.dto.MenuDto;
import com.andy.sapofnbcrawler.service.MenuService;

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
@RequestMapping(path = "${sapo-api.version}", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class SapoMenuController {
    
    private final MenuService menuService;
    
    @Operation(
    	summary = "Lấy danh sách menu hôm nay"	
    )
    @ApiResponses({
    	@ApiResponse(
			responseCode = "200", description = "Yêu cầu được thực hiện thành công",
			content = @Content(
				schema = @Schema(implementation = MenuDto.class)
			)
		),
    	@ApiResponse(
    		responseCode = "500", description = "Lấy thông tin menu không thành công, liên hệ với dev",
    		content = @Content(
				schema = @Schema(implementation = ErrorResponseDto.class)
			)
		)
    })
    @GetMapping("/menu")
    public ResponseEntity<?> getMenu() {
    	MenuDto menuResponse = menuService.getMenu();
		return ResponseEntity.ok(menuResponse);
    }
    
    @GetMapping("/api/hostname")
    public Map<String, String> getHostname(HttpServletRequest request) {
    	 Map<String, String> response = new HashMap<>();

         try {
             // Get remote IP address from request or use provided IP
             String remoteIp = request.getRemoteAddr();

             // Perform reverse DNS lookup
             InetAddress inetAddress = InetAddress.getByName(remoteIp);
             String hostname = inetAddress.getHostName();

             response.put("ipAddress: ", remoteIp);
             response.put("hostname: ", hostname);
             
             System.out.println("remoteIp" + remoteIp);
             System.out.println("hostname" + hostname);
             
         } catch (UnknownHostException e) {
             response.put("error", "Unable to resolve hostname for the given IP.");
         }

        
        return response;
    }
}
