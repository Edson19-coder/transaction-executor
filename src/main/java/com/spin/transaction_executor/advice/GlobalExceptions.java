package com.spin.transaction_executor.advice;

import com.spin.transaction_executor.domain.dto.ErrorDetailDTO;
import com.spin.transaction_executor.domain.response.list.ErrorDetailList;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptions {
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<?> badRequestException(Exception e) {
        List<ErrorDetailDTO> errors = new ArrayList<>();

        if (e instanceof MethodArgumentNotValidException manv) {
            manv.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(new ErrorDetailDTO(error.getField(), error.getDefaultMessage()))
            );

        } else if (e instanceof ConstraintViolationException cve) {

            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                String fullPath = violation.getPropertyPath().toString();
                String field = fullPath.substring(fullPath.lastIndexOf('.') + 1);
                errors.add(new ErrorDetailDTO(field, violation.getMessage()));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDetailList.builder().errors(errors).build());
    }
}
