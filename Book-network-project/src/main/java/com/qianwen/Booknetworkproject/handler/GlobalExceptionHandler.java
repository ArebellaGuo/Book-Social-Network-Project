package com.qianwen.Booknetworkproject.handler;

import com.qianwen.Booknetworkproject.exceptions.ActivationTokenException;
import com.qianwen.Booknetworkproject.exceptions.OperationNotPermittedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.qianwen.Booknetworkproject.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setBusinessErrorCode(ACCOUNT_LOCKED.getCode());
        exceptionResponse.setBusinessErrorDescription(ACCOUNT_LOCKED.getDescription());
        exceptionResponse.setError(exp.getMessage());
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(exceptionResponse);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setBusinessErrorCode(ACCOUNT_DISABLED.getCode());
        exceptionResponse.setBusinessErrorDescription(ACCOUNT_DISABLED.getDescription());
        exceptionResponse.setError(exp.getMessage());
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(exceptionResponse);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setBusinessErrorCode(BAD_CREDENTIALS.getCode());
        exceptionResponse.setBusinessErrorDescription(BAD_CREDENTIALS.getDescription());
        exceptionResponse.setError("Login and / or Password is incorrect");
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(exceptionResponse);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setError(exp.getMessage());
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(exceptionResponse);
    }

    @ExceptionHandler(ActivationTokenException.class)
    public ResponseEntity<ExceptionResponse> handleException(ActivationTokenException exp) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setError(exp.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(exceptionResponse);
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setError(exp.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();

        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        exceptionResponse.setValidationErrors(errors);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        exp.printStackTrace();
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setBusinessErrorDescription("Internal error, please contact the admin");
        exceptionResponse.setError(exp.getMessage());
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(exceptionResponse);
    }
}
