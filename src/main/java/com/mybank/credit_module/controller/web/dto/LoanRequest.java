package com.mybank.credit_module.controller.web.dto;

import com.mybank.credit_module.controller.web.validator.InstallmentAllowedIntValues;
import jakarta.validation.constraints.*;

public class LoanRequest {
    @NotNull(message = "Customer Id can not be null")
    private Long customerId;
    @NotNull(message = "Amount can not be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Requested amount must be greater than 0")
    private Double amount;
    @NotNull(message = "Interest rate can not be null")
    @DecimalMin(value = "0.1", inclusive = true, message = "Interest rate must be equal or greater than 0.1")
    @DecimalMax(value = "0.5", inclusive = true, message = "Interest rate must be equal or less than 0.5")
    private Double interestRate;
    @InstallmentAllowedIntValues
    private int numOfInstallments;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public int getNumOfInstallments() {
        return numOfInstallments;
    }

    public void setNumOfInstallments(int numOfInstallments) {
        this.numOfInstallments = numOfInstallments;
    }
}
