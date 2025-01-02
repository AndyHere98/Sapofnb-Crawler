package com.andy.sapofnbcrawler.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;

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
	@JsonProperty("apiPath")
	private String apiPath;
	@JsonProperty("errorStatus")
	private HttpStatus errorStatus;
	@JsonProperty("errorMessage")
	private String errorMessage;
	@JsonProperty("errorTime")
	private LocalDateTime errorTime;
}
