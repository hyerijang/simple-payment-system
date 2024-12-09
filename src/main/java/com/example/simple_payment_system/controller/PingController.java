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

}
