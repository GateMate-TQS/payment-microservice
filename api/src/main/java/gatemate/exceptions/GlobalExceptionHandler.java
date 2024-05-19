package gatemate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import gatemate.Generated;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Generated
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType != null && requiredType.isEnum()) {
            return new ResponseEntity<>("Invalid transaction status value", HttpStatus.BAD_REQUEST);
        }
      
        return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
    }
}
