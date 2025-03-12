package com.example.simple_payment_system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public enum ExceptionEnum {

    // System Exception
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST), ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    // COMMON Custom Exception
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    VALID_FAILED(HttpStatus.BAD_REQUEST, "요청값이 올바르지 않습니다."),

    // 포트원 결제,
    PORT_ONE_API_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "포트원 API 호출에 실패하였습니다."),
    PORT_ONE_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "포트원 결제 에 실패하였습니다."),
    PORT_ONE_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),

    // 결제 결과 처리
    PAYMENT_WEBHOOK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제 완료 처리에 실패하였습니다."),
    DUPLICATE_PAYMENT_CANCEL_REQUEST(HttpStatus.BAD_REQUEST, "중복된 취소 요청입니다."),
    ;

    private final HttpStatus status;
    private String message;
}
