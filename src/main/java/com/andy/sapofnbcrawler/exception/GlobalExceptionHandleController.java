package com.andy.sapofnbcrawler.exception;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.andy.sapofnbcrawler.dto.ErrorResponseDto;
import com.andy.sapofnbcrawler.dto.ErrorResponseDto.ErrorData;


@RestControllerAdvice
public class GlobalExceptionHandleController {
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDto> handleValidationOnRequest(MethodArgumentNotValidException exception, WebRequest webRequest) { 
		List<FieldError> errorList = exception.getFieldErrors();
		StringBuilder errorStrBuilder = new StringBuilder();
		
		ErrorResponseDto.ErrorData errorData = new ErrorData();
		List<ErrorData> errorDataList = new ArrayList<>();
		
		errorStrBuilder.append("Dữ liệu không hợp lệ vì ");
		for (FieldError error : errorList) {
			
			errorData = new ErrorData();
			errorData.setErrorName(error.getField());
			errorData.setErrorDesc(error.getDefaultMessage());
			errorDataList.add(errorData);
			
			errorStrBuilder.append(error.getDefaultMessage() + " ");
		}
		
		ErrorResponseDto errorResponse = new ErrorResponseDto(
				webRequest.getDescription(false),
				HttpStatus.BAD_REQUEST,
				errorStrBuilder.toString(),
				LocalDateTime.now()
			);
		errorResponse.setErrorDataList(errorDataList);
	
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(
					webRequest.getDescription(false),
					HttpStatus.NOT_FOUND,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}
	
	@ExceptionHandler(OrderExistedException.class)
	public ResponseEntity<ErrorResponseDto> handleOrderExistedException(OrderExistedException exception, WebRequest webRequest) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(
					webRequest.getDescription(false),
					HttpStatus.BAD_REQUEST,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.badRequest().body(errorResponse);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException exception, WebRequest webRequest) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(
					webRequest.getDescription(false),
					HttpStatus.BAD_REQUEST,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.badRequest().body(errorResponse);
	}
	

	@ExceptionHandler(ParseException.class)
	public ResponseEntity<ErrorResponseDto> handleParseException(ParseException exception, WebRequest webRequest) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(
					webRequest.getDescription(false),
					HttpStatus.BAD_REQUEST,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.badRequest().body(errorResponse);
	}
}
