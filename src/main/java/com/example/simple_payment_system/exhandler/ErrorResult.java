package com.example.simple_payment_system.exhandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;
}
