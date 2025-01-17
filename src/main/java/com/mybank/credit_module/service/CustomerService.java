package com.mybank.credit_module.service;

import com.mybank.credit_module.model.Customer;

import java.util.Optional;

public interface CustomerService {
    Optional<Customer> getCustomerById(Long customerId);

    Customer validateCustomer(Long customerId, Double amount);

    void updateCustomerUsedLimit(Customer customer, double usedCreditLimit);
}
