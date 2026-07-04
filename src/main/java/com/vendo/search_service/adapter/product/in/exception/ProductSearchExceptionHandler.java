package com.vendo.search_service.adapter.product.in.exception;

import com.vendo.search_service.domain.product.exception.InternalSearchException;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ProductSearchExceptionHandler {

    @ExceptionHandler(InternalSearchException.class)
    public ResponseEntity<ExceptionResponse> handleInternalSearchException(InternalSearchException e, HttpServletRequest request) {
        log.error("Internal search exception occurred. Reason: ", e);
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Internal search error.")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

}
