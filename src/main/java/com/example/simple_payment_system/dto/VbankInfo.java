package com.example.simple_payment_system.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VbankInfo {
    private String vbankCode;
    private String vbankName;
    private String vbankNum;
    private String vbankHolder;
    private Integer vbankDate;
}