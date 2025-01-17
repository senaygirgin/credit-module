package com.mybank.credit_module.service;

import com.mybank.credit_module.controller.error.CreditModuleValidationException;
import com.mybank.credit_module.model.Customer;
import com.mybank.credit_module.repository.CustomerRepository;
import com.mybank.credit_module.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    private CustomerService customerService;
    private Customer customer;

    @BeforeEach
    public void setUp(){
        this.customerService = new CustomerServiceImpl(customerRepository);
        customer = new Customer();
        customer.setId(1);
        customer.setName("Senay");
        customer.setSurname("Girgin");
        customer.setCreditLimit(1000);
        customer.setUsedCreditLimit(255.5);
    }

    @Test
    void validateCustomer_valid(){
        long customerId = 1;
        double requestedAmount = 105.5;

        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Assertions.assertDoesNotThrow(() -> customerService.validateCustomer(customerId, requestedAmount));
    }

    @Test
    void validateCustomer_insufficientLimit(){
        long customerId = 1;
        double requestedAmount = 855.5;

        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CreditModuleValidationException exc = Assertions.assertThrows(CreditModuleValidationException.class, () -> customerService.validateCustomer(customerId, requestedAmount));
        Assertions.assertTrue(exc.getMsg().contains("Insufficient credit limit for customerId"));

    }

    @Test
    void updateCustomerUsedLimit_increaseUsedLimit(){
        double usedLimit = -100;

        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        customerService.updateCustomerUsedLimit(customer, usedLimit);

        Assertions.assertEquals(155.5, customer.getUsedCreditLimit());

    }

}
