package com.andy.sapofnbcrawler.exception;

import java.text.ParseException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.andy.sapofnbcrawler.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandleController {

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse(
					webRequest.getDescription(false),
					HttpStatus.BAD_REQUEST,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.badRequest().body(errorResponse);
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
	
	@ExceptionHandler(DishNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleDishNotFoundException(DishNotFoundException exception, WebRequest webRequest) {
		ErrorResponse errorResponse = new ErrorResponse(
					webRequest.getDescription(false),
					HttpStatus.BAD_REQUEST,
					exception.getMessage(),
					LocalDateTime.now()
				);
		
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(ResponseNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResponseNotFoundException(ResponseNotFoundException exception, WebRequest webRequest) {
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
