package me.fasterthanlight.esdemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class EsDemoExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAppException(Exception exception, WebRequest request) {
        return ResponseEntity.status(getStatus(exception)).build();
    }

    private static HttpStatusCode getStatus(Throwable throwable) {
        return (throwable instanceof ErrorResponse errorResponse) ? errorResponse.getStatusCode() : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
