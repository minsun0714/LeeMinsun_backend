package com.wirebarley.codingtest.common.response;

import com.wirebarley.codingtest.common.exception.BaseException;

public record ErrorResponse(
        String code,
        String message
) {
    public static ErrorResponse from(BaseException e) {
        return new ErrorResponse(
                e.getClass().getSimpleName(),
                e.getMessage()
        );
    }

    public static ErrorResponse internal() {
        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "서버 오류가 발생했습니다."
        );
    }

    public static ErrorResponse notReadable(){
        return new ErrorResponse(
                "INVALID_REQUEST",
                "요청 메시지 형식이 잘못되었습니다."
        );
    }

    public static ErrorResponse validate(){
        return new ErrorResponse(
                "INVALID_REQUEST",
                "요청 형식이 올바르지 않습니다."
        );
    }

    public static ErrorResponse duplicate() {
        return new ErrorResponse(
                "DUPLICATE_RESOURCE",
                "이미 존재하는 값입니다."
        );
    }

    public static ErrorResponse methodNotAllowed() {
        return new ErrorResponse(
                "METHOD_NOT_ALLOWED",
                "지원하지 않는 HTTP 메서드입니다."
        );
    }
}