package ru.cft.advice;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import ru.cft.dto.ErrorDetailsDto;
import ru.cft.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            MissingServletRequestPartException.class,
            MissingRequestHeaderException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorDetailsDto> handleBadRequestExceptions(final Exception e, final WebRequest request) {
        return this.handleException(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
    })
    public ResponseEntity<ErrorDetailsDto> handleValidationExceptions(final MethodArgumentNotValidException e, final WebRequest request) {
        String message = "Validation failed: " + e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError)
                        return String.format("field `%s` %s", fieldError.getField(), fieldError.getDefaultMessage());
                    else
                        return String.format("%s", error.getDefaultMessage());

                })
                .collect(Collectors.joining(", "));
        return this.handleException(message, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetailsDto> handleResourceNotFoundException(final ResourceNotFoundException e,
                                                                           final WebRequest request) {
        return this.handleException(e, request, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorDetailsDto> handleException(final Exception e, final WebRequest request, final HttpStatus status) {
        return this.handleException(e.getMessage(), request, status);
    }

    private ResponseEntity<ErrorDetailsDto> handleException(final String message, final WebRequest request, final HttpStatus status) {
        var errorDto = new ErrorDetailsDto(LocalDateTime.now(), message, request.getDescription(false), status.value());
        log.error("Error: {}", errorDto);
        return ResponseEntity.status(status).body(errorDto);
    }
}
