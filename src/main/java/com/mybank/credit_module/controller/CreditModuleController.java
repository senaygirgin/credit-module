package com.mybank.credit_module.controller;

import com.mybank.credit_module.model.Customer;
import com.mybank.credit_module.model.Loan;
import com.mybank.credit_module.model.LoanInstallment;
import com.mybank.credit_module.service.CustomerService;
import com.mybank.credit_module.service.InstallmentService;
import com.mybank.credit_module.service.LoanService;
import com.mybank.credit_module.controller.web.ResponseHandler;
import com.mybank.credit_module.controller.web.dto.LoanRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/cm")
public class CreditModuleController {
    private static final Logger log = LoggerFactory.getLogger(CreditModuleController.class);

    private final CustomerService customerService;
    private final LoanService loanService;
    private final InstallmentService installmentService;

    public CreditModuleController(CustomerService customerService, LoanService loanService, InstallmentService installmentService) {

        this.customerService = customerService;
        this.loanService = loanService;
        this.installmentService = installmentService;
    }

    @PostMapping("/createLoan")
    @Transactional
    public ResponseEntity<Object> createLoan(@Valid @RequestBody LoanRequest loanRequest) {
        log.debug("createLoan request received");

        Customer customer = customerService.validateCustomer(loanRequest.getCustomerId(), loanRequest.getAmount());

        Loan loan = loanService.createLoan(loanRequest, customer);

        installmentService.createInstallments(loan);

        customerService.updateCustomerUsedLimit(customer, loan.getLoanAmount());

        return ResponseHandler.generateResponse("Loan granted successfully!", HttpStatus.CREATED, null);

    }

    @GetMapping("/listLoans")
    public ResponseEntity<Object> listLoans(@RequestParam long customerId) {

        List<Loan> loans = loanService.getLoanByCustomerId(customerId);
        return ResponseHandler.generateResponse("Successfully retrieved data!", HttpStatus.OK, loans);

    }

    @RequestMapping("/listInstallments")
    public ResponseEntity<Object> listInstallments(@RequestParam long loanId) {
        List<LoanInstallment> installments = installmentService.getInstallmentsByLoanId(loanId);
        return ResponseHandler.generateResponse("Successfully retrieved data!", HttpStatus.OK, installments);
    }


    @PutMapping("/payLoan")
    @Transactional
    public ResponseEntity<Object> payLoan(@RequestParam long loanId, @RequestParam double amount) {
        List<LoanInstallment> payedInstallments = installmentService.payInstallments(loanId, amount);

        if(!payedInstallments.isEmpty()){
            double paidAmountTotal = 0;
            for (LoanInstallment installment : payedInstallments){
                paidAmountTotal += installment.getPaidAmount();
            }

            boolean isLoanPaidCompletely = loanPaidCompletely(loanId);
            if(isLoanPaidCompletely) {
                loanService.setLoanPaid(loanId);
            }
            customerService.updateCustomerUsedLimit(payedInstallments.get(0).getLoan().getCustomer(), (-1 * paidAmountTotal));
            return ResponseHandler.generateResponse("Payment information listed!", HttpStatus.OK, createPayLoanResponse(payedInstallments.size(), paidAmountTotal, isLoanPaidCompletely));
        }
        return ResponseHandler.generateResponse("No payment done!", HttpStatus.OK, createPayLoanResponse(0, 0.0, false));
    }

    private boolean loanPaidCompletely(long loanId) {
        List<LoanInstallment> installments = installmentService.getInstallmentsByLoanId(loanId);
        for (LoanInstallment installment : installments){
            if(!installment.isPaid())
                return false;
        }
        return true;
    }

    private Map<String, String> createPayLoanResponse(int paidInstallments, double paidAmountTotal, boolean isLoanPaid) {
        Map<String, String> payLoanResponse = new HashMap<>();
        payLoanResponse.put("paidInstallmentCount", String.valueOf(paidInstallments));
        payLoanResponse.put("totalAmountSpent", String.valueOf(paidAmountTotal));
        if(paidInstallments == 0){
            payLoanResponse.put("LoanPaidStatus", "Insufficient amount to pay an installment");
        }else {
            payLoanResponse.put("LoanPaidStatus", isLoanPaid ? "Loan is paid completely" : "Loan is paid partially");
        }

        return payLoanResponse;
    }
}
