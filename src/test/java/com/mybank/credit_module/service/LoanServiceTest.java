package com.mybank.credit_module.service;

import com.mybank.credit_module.controller.error.CreditModuleValidationException;
import com.mybank.credit_module.model.Customer;
import com.mybank.credit_module.model.Loan;
import com.mybank.credit_module.repository.LoanRepository;
import com.mybank.credit_module.service.impl.LoanServiceImpl;
import com.mybank.credit_module.controller.web.dto.LoanRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {
    @Mock
    private LoanRepository loanRepository;
    @Captor
    private ArgumentCaptor<Loan> loanArgumentCaptor;

    private LoanService loanService;

    @BeforeEach
    public void setUp() {
        loanService = new LoanServiceImpl(loanRepository);
    }

    @Test
    void createLoan(){
        LoanRequest loanReq = new LoanRequest();
        loanReq.setAmount(100D);
        loanReq.setInterestRate(0.25);
        loanReq.setNumOfInstallments(12);

        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("Senay");
        customer.setSurname("Girgin");
        customer.setCreditLimit(1000);
        customer.setUsedCreditLimit(255.5);

        loanService.createLoan(loanReq, customer);

        Mockito.verify(loanRepository).save(loanArgumentCaptor.capture());

        Loan createdLoan = loanArgumentCaptor.getValue();
        Assertions.assertEquals(100 * 1.25, createdLoan.getLoanAmount());
        Assertions.assertEquals(12, createdLoan.getNumberOfInstallment());
        Assertions.assertEquals(LocalDate.now(), createdLoan.getCreateDate());
        Assertions.assertEquals(customer.getId(), createdLoan.getCustomer().getId());
        Assertions.assertTrue(!createdLoan.isPaid());
    }

    @Test
    void setLoanPaid_success(){
        long loanId = 2222;

        Loan loan = new Loan();
        loan.setLoanAmount(1500);
        loan.setPaid(false);


        Mockito.when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        Mockito.when(loanRepository.save(loan)).thenReturn(loan);

        loanService.setLoanPaid(loanId);

        Assertions.assertTrue(loan.isPaid());

    }

    @Test
    void setLoanPaid_throwExc(){
        long loanId = 2222;

        Mockito.when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        Assertions.assertThrows(CreditModuleValidationException.class, () -> loanService.setLoanPaid(loanId));

    }
}
