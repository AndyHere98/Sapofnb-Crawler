package com.andy.sapofnbcrawler.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(
	name = "Error response",
	description = "Response phản hồi khi xảy ra lỗi"
)
@Data
@JsonRootName("response")
@JsonInclude(Include.NON_NULL)
public class ErrorResponse {
	
	public ErrorResponse(String apiPath, HttpStatus errorStatus, String errorMessage, LocalDateTime errorTime) {
		this.apiPath = apiPath;
		this.errorStatus = errorStatus;
		this.errorMessage = errorMessage;
		this.errorTime = errorTime;
	}
	
	@Schema(
		description = "Địa chỉ url đang lỗi"
	)
	@JsonProperty("apiPath")
	private String apiPath;
	
	@Schema(
		description = "HTTP status code"
	)
	@JsonProperty("errorStatus")
	private HttpStatus errorStatus;
	
	@Schema(
		description = "Thông tin lỗi"
	)
	@JsonProperty("errorMessage")
	private String errorMessage;
	
	@Schema(
		description = "Thời gian xuất hiện lỗi"
	)
	@JsonProperty("errorTime")
	private LocalDateTime errorTime;
}
