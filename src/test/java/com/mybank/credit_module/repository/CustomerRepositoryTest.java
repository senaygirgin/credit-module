package com.mybank.credit_module.repository;

import com.mybank.credit_module.model.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp(){}

    @Test
    public void createCustomer(){
        long customerCountBefore = customerRepository.count();

        Customer customer = new Customer();
        customer.setName("Senay");
        customer.setSurname("Girgin");
        customer.setCreditLimit(1000);
        customer.setUsedCreditLimit(375);

        customerRepository.save(customer);

        long customerCountAfter = customerRepository.count();
        Assertions.assertThat(customerCountAfter).isEqualTo(customerCountBefore+1);

    }
}
