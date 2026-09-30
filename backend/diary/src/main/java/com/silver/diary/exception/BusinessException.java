package com.silver.diary.exception;

/** 可以向用户展示提示信息的业务异常。 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}