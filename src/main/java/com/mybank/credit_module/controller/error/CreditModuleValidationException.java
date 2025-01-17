package com.mybank.credit_module.controller.error;

public class CreditModuleValidationException extends RuntimeException{

    private int errorCode;
    private String msg;

    public CreditModuleValidationException(ErrorEnum errorEnum, String msg) {
        this.errorCode = errorEnum.getCode();
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
