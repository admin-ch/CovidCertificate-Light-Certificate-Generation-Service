package ch.admin.bag.covidcertificate.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ResponseStatusExceptionHandler {

    @ExceptionHandler(value = {CreateCertificateException.class})
    protected ResponseEntity<Object> handleCreateCertificateException(CreateCertificateException ex) {
        if (ex.getError().getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error(ex.getError().getErrorMessage(), ex);
            var error = ex.getError();
            if (error != null) {
                return new ResponseEntity<>(ex.getError(), ex.getError().getHttpStatus());
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Create certificate exception, errorCode: {}", ex.getError().getErrorCode(), ex);
            return new ResponseEntity<>(ex.getError(), ex.getError().getHttpStatus());
        }
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errorFormatString = "%s.%s: %s, %s";

        var errors = ex.getFieldErrors().stream().map(fieldError ->
                String.format(errorFormatString, fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage())
         ).collect(Collectors.joining("\n"));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
