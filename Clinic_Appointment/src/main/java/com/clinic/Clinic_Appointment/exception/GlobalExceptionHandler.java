package com.clinic.Clinic_Appointment.exception;

import com.clinic.Clinic_Appointment.dto.Dto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Custom exceptions ─────────────────────────────────────────────────────

    //if record not found
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) { super(message); }
    }

    // business rule conflict
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) { super(message); }
    }

    //doing wrong stuff
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) { super(message); }
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Dto.ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Dto.ErrorResponse(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Dto.ErrorResponse> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new Dto.ErrorResponse(409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Dto.ErrorResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Dto.ErrorResponse(400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Dto.ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Dto.ErrorResponse(400, "Validation Failed", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Dto.ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Dto.ErrorResponse(500, "Internal Server Error", "An unexpected error occurred"));
    }
}
