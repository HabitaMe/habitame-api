package com.habitame.api.common.exception;

import com.habitame.api.common.wrapper.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 400 - Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return buildError(
                HttpStatus.BAD_REQUEST,
                ApiError.VALIDATION_ERROR,
                message,
                request.getRequestURI()
        );
    }

    // 401 - Unauthorized
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.UNAUTHORIZED,
                ApiError.UNAUTHORIZED_EXCEPTION,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // 403 - Forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.FORBIDDEN,
                ApiError.ACCESS_DENIED,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // 403 - Spring Security (@PreAuthorize, etc.)
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.FORBIDDEN,
                ApiError.ACCESS_DENIED,
                "Access denied: " + ex.getMessage(),
                request.getRequestURI()
        );
    }

    // 404 - Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.NOT_FOUND,
                ApiError.RESOURCE_NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // 409 - Conflict
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.CONFLICT,
                ApiError.DUPLICATE_RESOURCE,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // 500 - Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ApiError.UNEXPECTED_ERROR,
                "An unexpected error occurred",
                request.getRequestURI()
        );
    }

    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status,
            ApiError errorType,
            String message,
            String path) {

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(errorType)
                .message(message)
                .path(path)
                .build();

        return ResponseEntity.status(status).body(error);
    }
}