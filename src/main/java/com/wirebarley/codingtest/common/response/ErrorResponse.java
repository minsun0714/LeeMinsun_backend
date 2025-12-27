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
}