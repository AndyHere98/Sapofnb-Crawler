package com.andy.sapofnbcrawler.exception;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.andy.sapofnbcrawler.response.ErrorResponse;


@RestControllerAdvice
public class GlobalExceptionHandleController {
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationOnRequest(MethodArgumentNotValidException exception, WebRequest webRequest) { 
		List<ObjectError> errorList = exception.getAllErrors();
		StringBuilder errorStrBuilder = new StringBuilder();
		errorStrBuilder.append("Dữ liệu không hợp lệ vì ");
		for (ObjectError error : errorList) {
			errorStrBuilder.append(error.getDefaultMessage() + " ");
		}
		
		ErrorResponse errorResponse = new ErrorResponse(
				webRequest.getDescription(false),
				HttpStatus.BAD_REQUEST,
				errorStrBuilder.toString(),
				LocalDateTime.now()
			);
	
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse(
					webRequest.getDescription(false),
					HttpStatus.NOT_FOUND,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}
	
	@ExceptionHandler(OrderExistedException.class)
	public ResponseEntity<ErrorResponse> handleOrderExistedException(OrderExistedException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse(
					webRequest.getDescription(false),
					HttpStatus.BAD_REQUEST,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.badRequest().body(errorResponse);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse(
					webRequest.getDescription(false),
					HttpStatus.BAD_REQUEST,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.badRequest().body(errorResponse);
	}
	

	@ExceptionHandler(ParseException.class)
	public ResponseEntity<ErrorResponse> handleParseException(ParseException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse(
					webRequest.getDescription(false),
					HttpStatus.BAD_REQUEST,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.badRequest().body(errorResponse);
	}
}
