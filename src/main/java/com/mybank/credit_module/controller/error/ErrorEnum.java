package com.mybank.credit_module.controller.error;

public enum ErrorEnum {

    CUSTOMER_NOT_FOUND(1000, "Unknown customer"),
    CUSTOMER_CREDIT_LIMIT_INSUFFICIENT(1010, "Insufficient credit limit"),

    LOAN_NOT_FOUND(2000, "Unknown loan");

    private int code;
    private String desc;

    ErrorEnum(int code, String msg) {
        this.code = code;
        this.desc = msg;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
