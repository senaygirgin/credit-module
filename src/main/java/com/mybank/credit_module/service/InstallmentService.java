package com.mybank.credit_module.service;

import com.mybank.credit_module.model.Loan;
import com.mybank.credit_module.model.LoanInstallment;

import java.util.List;

public interface InstallmentService {
    void createInstallments(Loan loan);

    List<LoanInstallment> getInstallmentsByLoanId(long loanId);

    List<LoanInstallment> payInstallments(long loanId, double amount);
}
