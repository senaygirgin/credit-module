package com.mybank.credit_module.service.impl;

import com.mybank.credit_module.controller.error.CreditModuleValidationException;
import com.mybank.credit_module.controller.error.ErrorEnum;
import com.mybank.credit_module.model.Customer;
import com.mybank.credit_module.repository.CustomerRepository;
import com.mybank.credit_module.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers(){
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public Customer validateCustomer(Long customerId, Double requestedAmount) {
        Optional<Customer> customerOpt = getCustomerById(customerId);

        // TODO: 1/15/2025 move to validator class
        if(customerOpt.isEmpty()){
            throw new CreditModuleValidationException(ErrorEnum.CUSTOMER_NOT_FOUND, "Unknown customer with customerId : " + customerId);
        }

        Customer customer = customerOpt.get();

        if((customer.getCreditLimit() - customer.getUsedCreditLimit()) < requestedAmount){
            throw new CreditModuleValidationException(ErrorEnum.CUSTOMER_CREDIT_LIMIT_INSUFFICIENT, "Insufficient credit limit for customerId : " + requestedAmount);
        }
        return customer;
    }

    @Override
    public void updateCustomerUsedLimit(Customer customer, double usedCreditLimit) {
        double usedLimitBefore = customer.getUsedCreditLimit();
        customer.setUsedCreditLimit(usedCreditLimit + usedLimitBefore);

        customerRepository.save(customer);
    }
}
