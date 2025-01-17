package com.mybank.credit_module.service;

import com.mybank.credit_module.model.Customer;
import com.mybank.credit_module.model.Loan;
import com.mybank.credit_module.model.LoanInstallment;
import com.mybank.credit_module.repository.InstallmentRepository;
import com.mybank.credit_module.service.impl.InstallmentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class InstallmentServiceTest {
    @Mock
    private InstallmentRepository installmentRepository;

    @Captor
    private ArgumentCaptor<List<LoanInstallment>> installmentListsArgumentCaptor;

    @Captor
    private ArgumentCaptor<LoanInstallment> installmentArgumentCaptor;

    private InstallmentService installmentService;

    private Loan loan;

    @BeforeEach
    public void setUp() {

        loan = new Loan();
        loan.setId(1111);
        loan.setLoanAmount(255.5);
        loan.setPaid(false);
        loan.setCustomer(new Customer());
        loan.setNumberOfInstallment(9);
        loan.setCreateDate(LocalDate.now());

        this.installmentService = new InstallmentServiceImpl(installmentRepository);
    }

    @Test
    void createInstallments_installmentsCreatedForGivenLoan(){

        List<LoanInstallment> createdInstallments = getLoanInstallments();

        Assertions.assertEquals(createdInstallments.size(), loan.getNumberOfInstallment());
        Assertions.assertEquals(createdInstallments.get(0).getAmount(), loan.getLoanAmount() / loan.getNumberOfInstallment());
        Assertions.assertEquals(createdInstallments.get(0).getPaidAmount(), 0);
        Assertions.assertEquals(createdInstallments.get(0).getLoan().getId(), loan.getId());
        Assertions.assertTrue(!createdInstallments.get(0).isPaid());

        LoanInstallment fistInstallment = createdInstallments.stream().sorted(Comparator.comparing(LoanInstallment::getDueDate)).findFirst().get();
        LoanInstallment lastInstallment = createdInstallments.stream().sorted(Comparator.comparing(LoanInstallment::getDueDate).reversed()).findFirst().get();
        LocalDate firstDueDate = LocalDate.now().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDueDate = LocalDate.now().plusMonths(loan.getNumberOfInstallment()).with(TemporalAdjusters.firstDayOfMonth());

        Assertions.assertEquals(fistInstallment.getDueDate(), firstDueDate);
        Assertions.assertEquals(lastInstallment.getDueDate(), lastDueDate);

    }

    private List<LoanInstallment> getLoanInstallments() {
        installmentService.createInstallments(loan);

        Mockito.verify(installmentRepository).saveAll(installmentListsArgumentCaptor.capture());

        List<LoanInstallment> createdInstallments = installmentListsArgumentCaptor.getValue();
        return createdInstallments;
    }


    @Test
    void payInstallments_success_oneInstallPaid(){
        double amountToBePaid = 30;
        List<LoanInstallment> unPaidInstallments = getLoanInstallments();

        Mockito.when(installmentRepository.findByLoanId(loan.getId())).thenReturn(unPaidInstallments);

        installmentService.payInstallments(loan.getId(), amountToBePaid);

        Mockito.verify(installmentRepository).save(installmentArgumentCaptor.capture());

        List<LoanInstallment>  paidInstallments = installmentArgumentCaptor.getAllValues();

        Assertions.assertEquals(paidInstallments.size(), 1);
        Assertions.assertEquals(paidInstallments.get(0).getPaymentDate(), LocalDate.now());
        Assertions.assertEquals(paidInstallments.get(0).getPaidAmount(), loan.getLoanAmount() / loan.getNumberOfInstallment());
        Assertions.assertTrue(paidInstallments.get(0).isPaid());

    }

    @Test
    void payInstallments_success_moreInstallmentPaidInAdvance_succees(){

        double amountToBePaid = 60;
        int installmentCountShouldBePaid = 2;
        List<LoanInstallment> unPaidInstallments = getLoanInstallments();

        Mockito.when(installmentRepository.findByLoanId(loan.getId())).thenReturn(unPaidInstallments);

        installmentService.payInstallments(loan.getId(), amountToBePaid);

        Mockito.verify(installmentRepository, Mockito.times(installmentCountShouldBePaid)).save(installmentArgumentCaptor.capture());

        List<LoanInstallment>  paidInstallments = installmentArgumentCaptor.getAllValues();

        Assertions.assertEquals(paidInstallments.size(), installmentCountShouldBePaid);
        for(int i = 0 ; i< paidInstallments.size(); i++) {
            Assertions.assertEquals(paidInstallments.get(i).getPaymentDate(), LocalDate.now());
            Assertions.assertEquals(paidInstallments.get(i).getPaidAmount(), loan.getLoanAmount() / loan.getNumberOfInstallment());
            Assertions.assertTrue(paidInstallments.get(i).isPaid());
        }
    }

    @Test
    void payInstallments_success_morePastInstallmentPaid_succes(){
        double amountToBePaid = 120;
        int installmentCountShouldBePaid = 4;

        List<LoanInstallment> unPaidInstallments = getLoanInstallments();

        for(int i= 0; i<installmentCountShouldBePaid; i++){
            unPaidInstallments.get(i).setDueDate(LocalDate.now().minusMonths(i));
        }

        Mockito.when(installmentRepository.findByLoanId(loan.getId())).thenReturn(unPaidInstallments);

        installmentService.payInstallments(loan.getId(), amountToBePaid);

        Mockito.verify(installmentRepository, Mockito.times(installmentCountShouldBePaid)).save(installmentArgumentCaptor.capture());

        List<LoanInstallment>  paidInstallments = installmentArgumentCaptor.getAllValues();

        Assertions.assertEquals(paidInstallments.size(), installmentCountShouldBePaid);
        for(int i = 0 ; i< paidInstallments.size(); i++) {
            Assertions.assertEquals(paidInstallments.get(i).getPaymentDate(), LocalDate.now());
            Assertions.assertEquals(paidInstallments.get(i).getPaidAmount(), loan.getLoanAmount() / loan.getNumberOfInstallment());
            Assertions.assertTrue(paidInstallments.get(i).isPaid());
        }
    }
    @Test
    void payInstallments_success_moreInstallmentPaidInAdvance_partialSuccees(){
        double amountToBePaid = 120;
        int installmentCountShouldBePaid = 2;
        List<LoanInstallment> unPaidInstallments = getLoanInstallments();

        Mockito.when(installmentRepository.findByLoanId(loan.getId())).thenReturn(unPaidInstallments);

        installmentService.payInstallments(loan.getId(), amountToBePaid);

        Mockito.verify(installmentRepository, Mockito.times(installmentCountShouldBePaid)).save(installmentArgumentCaptor.capture());

        List<LoanInstallment>  paidInstallments = installmentArgumentCaptor.getAllValues();

        Assertions.assertEquals(paidInstallments.size(), installmentCountShouldBePaid);
        for(int i = 0 ; i< paidInstallments.size(); i++) {
            Assertions.assertEquals(paidInstallments.get(i).getPaymentDate(), LocalDate.now());
            Assertions.assertEquals(paidInstallments.get(i).getPaidAmount(), loan.getLoanAmount() / loan.getNumberOfInstallment());
            Assertions.assertTrue(paidInstallments.get(i).isPaid());
        }

    }

    @Test
    void payInstallments_success_someInstallmentsPaidBefore(){
        double amountToBePaid = 30;
        int installmentCountShouldBePaid = 1;
        int paidInstallmentCountBefore = 2;

        List<LoanInstallment> unPaidInstallments = getLoanInstallments();

        for(int i= 0; i<installmentCountShouldBePaid + paidInstallmentCountBefore; i++){
            unPaidInstallments.get(i).setDueDate(LocalDate.now().minusMonths(i));
        }

        List<LoanInstallment> sortedUnPaidInstallments = unPaidInstallments.stream()
                .sorted(Comparator.comparing(LoanInstallment::getDueDate))
                .collect(Collectors.toList());

        List<LoanInstallment> somePaidInstallments = sortedUnPaidInstallments.stream()
                        .map(installment -> {
                            int i = sortedUnPaidInstallments.indexOf(installment);
                            if(i<paidInstallmentCountBefore){
                                installment.setPaid(true);
                                installment.setPaymentDate(LocalDate.now());
                                installment.setPaidAmount(installment.getAmount());
                            }
                            return installment;
                        })
                .collect(Collectors.toList());



        Mockito.when(installmentRepository.findByLoanId(loan.getId())).thenReturn(somePaidInstallments);

        installmentService.payInstallments(loan.getId(), amountToBePaid);

        Mockito.verify(installmentRepository).save(installmentArgumentCaptor.capture());

        List<LoanInstallment>  paidInstallments = installmentArgumentCaptor.getAllValues();

        Assertions.assertEquals(paidInstallments.size(), installmentCountShouldBePaid);
        Assertions.assertEquals(paidInstallments.get(0).getPaymentDate(), LocalDate.now());
        Assertions.assertEquals(paidInstallments.get(0).getPaidAmount(), loan.getLoanAmount() / loan.getNumberOfInstallment());
        Assertions.assertTrue(paidInstallments.get(0).isPaid());

    }

    @Test
    void payInstallments_fail_notEnough(){
        double amountToBePaid = 10;
        int installmentCountShouldBePaid = 0;

        List<LoanInstallment> unPaidInstallments = getLoanInstallments();

        Mockito.when(installmentRepository.findByLoanId(loan.getId())).thenReturn(unPaidInstallments);

        installmentService.payInstallments(loan.getId(), amountToBePaid);

        Mockito.verify(installmentRepository, Mockito.times(installmentCountShouldBePaid)).save(installmentArgumentCaptor.capture());

        List<LoanInstallment>  paidInstallments = installmentArgumentCaptor.getAllValues();

        Assertions.assertEquals(paidInstallments.size(), installmentCountShouldBePaid);

    }
}
