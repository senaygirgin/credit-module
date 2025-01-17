package com.mybank.credit_module.controller.web.validator;

public enum InstallmentAllowedValues {

    SIX(6),
    NINE(9),
    TWELVE(12),
    TWENTYFOUR(24);

    private int numOfInstallment;

    InstallmentAllowedValues(int numOfInstallment) {
        this.numOfInstallment = numOfInstallment;
    }

    public int getNumOfInstallment() {
        return numOfInstallment;
    }

    public static boolean contains(int value) {
        for(InstallmentAllowedValues allowedvalue : InstallmentAllowedValues.values()){
            if(allowedvalue.numOfInstallment == value)
                return true;
        }
        return false;
    }
}
