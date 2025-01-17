package com.mybank.credit_module.service.impl;

import com.mybank.credit_module.model.Loan;
import com.mybank.credit_module.model.LoanInstallment;
import com.mybank.credit_module.repository.InstallmentRepository;
import com.mybank.credit_module.service.InstallmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstallmentServiceImpl implements InstallmentService {
    private final InstallmentRepository installmentRepository;

    //@Value("{service.installment.count.pay.inadvance}")
    private final int installmentCountToPayInadvance = 3;

    public InstallmentServiceImpl(InstallmentRepository installmentRepository) {
        this.installmentRepository = installmentRepository;
    }

    @Override
    public void createInstallments(Loan loan) {
// TODO: 1/15/2025 saveAndFlush method of repository?
        List<LoanInstallment> installments = initiateInstallments(loan);
        System.out.println("installments :" + installments);
        installmentRepository.saveAll(installments);

    }

    @Override
    public List<LoanInstallment> getInstallmentsByLoanId(long loanId) {
        return installmentRepository.findByLoanId(loanId);
    }

    @Override
    public List<LoanInstallment> payInstallments(long loanId, double amount) {
        List<LoanInstallment> payedInstallments = new ArrayList<>();
        List<LoanInstallment> unpaidInstallments = installmentRepository.findByLoanId(loanId)
                .stream()
                .filter(installment -> !installment.isPaid())
                .collect(Collectors.toList());

        if(!unpaidInstallments.isEmpty()){
            double installmentAmount = unpaidInstallments.get(0).getAmount();
            int payableInstallmentCount = (int) (amount / installmentAmount);

            payedInstallments = unpaidInstallments.stream()
                    .sorted(Comparator.comparing(LoanInstallment::getDueDate))
                    .filter(installment -> installment.getDueDate().isBefore(getFirstDayOfNextMonth(LocalDate.now(), installmentCountToPayInadvance)))
                    .limit(payableInstallmentCount)
                    .map(installment ->
                    {
                        installment.setPaidAmount(installment.getAmount());
                        installment.setPaymentDate(LocalDate.now());
                        installment.setPaid(true);
                        return installmentRepository.save(installment);
                    })
                    .collect(Collectors.toList());

        }

        return payedInstallments;

    }

    private List<LoanInstallment> initiateInstallments(Loan loan) {
        double installmentAmount = loan.getLoanAmount()/loan.getNumberOfInstallment();
        int installmentNum = loan.getNumberOfInstallment();;

        List<LoanInstallment> installments = new ArrayList<>();
        for(int i = 0; i < installmentNum; i++) {
            LocalDate firstDayOfMonth = getFirstDayOfNextMonth(LocalDate.now(),i+1);

            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setPaidAmount(0.0);
            installment.setDueDate(firstDayOfMonth);
            installment.setPaymentDate(null);
            installment.setPaid(false);

            installments.add(installment);
        }
        return installments;
    }

    private LocalDate getFirstDayOfNextMonth(LocalDate date, int howManyMonthNext) {

        return date.plusMonths(howManyMonthNext).with(TemporalAdjusters.firstDayOfMonth());
    }
}
