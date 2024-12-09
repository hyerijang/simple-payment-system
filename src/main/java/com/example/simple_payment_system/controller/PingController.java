package com.example.simple_payment_system.controller;

import static com.example.simple_payment_system.exception.ExceptionEnum.*;
import com.example.simple_payment_system.exception.CustomApiException;
import java.io.FileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/api/v1/exceptions/status/404")
    public ResponseEntity<Void> status404() {
        throw new CustomApiException(RUNTIME_EXCEPTION, "404 error");
    }

    @GetMapping("/api/v1/exceptions/status/500")
    public ResponseEntity<Void> status500() {
        throw new CustomApiException(INTERNAL_SERVER_ERROR, "500 error");
    }

    @GetMapping("/api/v1/exceptions/status/401")
    public ResponseEntity<Void> status401() {
        throw new CustomApiException(ACCESS_DENIED_EXCEPTION, "401 error");
    }

    @GetMapping("/api/v1/exceptions")
    public ResponseEntity<Void> not() throws FileNotFoundException {
        throw new FileNotFoundException("File not found!!!");
    }

    @GetMapping("/api/v1/exceptions/2")
    public ResponseEntity<Void> illegalArgumentException() {
        throw new IllegalArgumentException("IllegalArgumentException");
    }
}
