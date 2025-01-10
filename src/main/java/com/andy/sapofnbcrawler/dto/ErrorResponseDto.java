package com.andy.sapofnbcrawler.dto;

import java.time.LocalDateTime;
import java.util.List;

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
public class ErrorResponseDto {
	
	public ErrorResponseDto(String apiPath, HttpStatus errorStatus, String errorMessage, LocalDateTime errorTime) {
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
		description = "Thông tin chi tiết lỗi"
	)
	@JsonProperty("errorData")
	private List<ErrorData> errorDataList;
	
	@Schema(
		description = "Thời gian xuất hiện lỗi"
	)
	@JsonProperty("errorTime")
	private LocalDateTime errorTime;
	
	@Schema(
		name = "Thông tin hiển thị lỗi cho validation",
		description = "Hiển thị chi tiết lỗi được mô tả"
	)
	@Data
    @JsonInclude(Include.NON_NULL)
	public static class ErrorData {
		@Schema(
			description = "Tên giá trị đang lỗi"
		)
		@JsonProperty("errorName")
		private String errorName;
		@Schema(
				description = "Mô tả nguyên nhân giá trị đang lỗi"
			)
		@JsonProperty("errorDesc")
		private String errorDesc;
	}
}
