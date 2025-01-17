package com.mybank.credit_module.service;

import com.mybank.credit_module.model.Customer;
import com.mybank.credit_module.model.Loan;
import com.mybank.credit_module.controller.web.dto.LoanRequest;

import java.util.List;

public interface LoanService {
    Loan createLoan(LoanRequest loanRequest, Customer customer);

    List<Loan> getLoanByCustomerId(long customerId);

    Loan setLoanPaid(long loanId);
}
