package com.example.deliverygo;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;


//https://stackoverflow.com/questions/68872626/ambiguous-exception-when-using-webflux-bean-validation-webexchangebindexception
import javax.validation.ConstraintViolationException;
@Slf4j
@RestControllerAdvice
public class GlobalErrorController {

	/*@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<?> handleException(WebExchangeBindException e) {
		log.info("WebExchangeBindException : {}", e);
		return ResponseEntity.ok()
				.body(Void.class);
	}

*/

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleException(ConstraintViolationException e) {
		log.info("ConstraintViolationException : {}", e);
		var errors = e
				.getConstraintViolations()
				.stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());
		return ResponseEntity.badRequest().body(errors);
	}
	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<List<String>> handleException(WebExchangeBindException e) {
		log.info("WebExchangeBindException : {}", e);
		var errors = e.getBindingResult()
				.getAllErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.toList());
		return ResponseEntity.badRequest().body(errors);
	}
}
