package com.andy.sapofnbcrawler.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
	name = "Response chung",
	description = "Response thông thường trả về dùng trong tất cả messages khi request thực hiện không hoạt động đúng như yêu cầu"
)
@Data
@JsonRootName("response")
@JsonInclude(Include.NON_NULL)
public class CommonResponse {
	
	@Schema(
		description = "HTTP status code"
	)
	@JsonProperty("status")
	private HttpStatus status;
	
	@Schema(
		description = "HTTP status Message"
	)
	@JsonProperty("message")
	private String message;
	
	@Schema(
		description = "Data object if needed to show"
	)
	@JsonProperty("data")
	private Object data;
}
