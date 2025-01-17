package com.mybank.credit_module.service.impl;

import com.mybank.credit_module.controller.error.CreditModuleValidationException;
import com.mybank.credit_module.controller.error.ErrorEnum;
import com.mybank.credit_module.model.Customer;
import com.mybank.credit_module.model.Loan;
import com.mybank.credit_module.repository.LoanRepository;
import com.mybank.credit_module.service.LoanService;
import com.mybank.credit_module.controller.web.dto.LoanRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan createLoan(LoanRequest loanRequest, Customer customer) {
        Loan loan = initializeLoan(loanRequest, customer);
        System.out.println("loan before db:" + loan);
        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> getLoanByCustomerId(long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    @Override
    public Loan setLoanPaid(long loanId) {
        Loan payedLoan = loanRepository.findById(loanId)
                .map(loan -> {
                    loan.setPaid(true);
                    return loanRepository.save(loan);
                })
                .orElseThrow(() -> new CreditModuleValidationException(ErrorEnum.LOAN_NOT_FOUND, "Loan is not found with give loanId : " + loanId));

        return payedLoan;
    }

    private Loan initializeLoan(LoanRequest loanRequest, Customer customer) {

        double loanAmountWithInterest = loanRequest.getAmount() * (1 + loanRequest.getInterestRate());

        Loan loan = new Loan();
        loan.setLoanAmount(loanAmountWithInterest);
        loan.setPaid(false);
        loan.setCustomer(customer);
        loan.setNumberOfInstallment(loanRequest.getNumOfInstallments());
        loan.setCreateDate(LocalDate.now());
        return loan;

    }

}
