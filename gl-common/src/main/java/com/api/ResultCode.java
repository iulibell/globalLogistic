package com.api;

public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAIL(400, "操作失败"),
    VALIDATION_ERROR(404, "参数校验失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    INTERNAL_ERROR(500, "内部错误");
    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
