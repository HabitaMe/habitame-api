package com.habitame.api.common.exception;

import com.habitame.api.common.wrapper.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 404 - Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request){

        return buildError(
                HttpStatus.NOT_FOUND,
                ApiError.RESOURCE_NOT_FOUND, // Enum fijo
                ex.getMessage(),             // Mensaje dinámico ("Property not found: 4")
                request.getRequestURI()
        );
    }

    // 409 - Conflict
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request){

        return buildError(
                HttpStatus.CONFLICT,
                ApiError.DUPLICATE_RESOURCE, // Asegúrate de tener este Enum o usa uno genérico
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // 400 - Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request){

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return buildError(
                HttpStatus.BAD_REQUEST,
                ApiError.VALIDATION_ERROR, // Asegúrate de tener este Enum
                message,
                request.getRequestURI()
        );
    }

    // 500 - Fallback / Generic
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request){

        log.error("Unhandled exception", ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ApiError.UNEXPECTED_ERROR,
                "An unexpected error occurred", // Mensaje genérico por seguridad
                request.getRequestURI()
        );
    }

    // MÉTODO HELPER CORREGIDO
    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status,
            ApiError errorType,   // La categoría del error (Enum)
            String detailMessage, // La descripción específica (String)
            String path){

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(errorType) // Usamos el nombre del Enum (ej. "RESOURCE_NOT_FOUND")
                .message(detailMessage)  // Usamos el mensaje de texto real
                .path(path)
                .build();

        return ResponseEntity.status(status).body(error);
    }
}
