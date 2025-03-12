package com.example.simple_payment_system.exhandler.advice;


import com.example.simple_payment_system.exception.CustomApiException;
import com.example.simple_payment_system.exhandler.ErrorResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler({CustomApiException.class})
    public ResponseEntity<ErrorResult> apiExceptionHandler(HttpServletRequest request, final CustomApiException e) {
        log.error("[exceptionHandle] api ex", e);
        return ResponseEntity
            .status(e.getError().getStatus())
            .body(ErrorResult.builder()
                .code(e.getError().name())
                .message(e.getMessage())
                .build());
    }


    // 예기치 못한 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResult> illegalExHandle(IllegalArgumentException e) {
        log.error("[exceptionHandle] bad", e);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResult("BAD", e.getMessage()));
    }

    @ExceptionHandler
    ResponseEntity<ErrorResult> exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResult("EX", "내부 오류"));
    }

}
