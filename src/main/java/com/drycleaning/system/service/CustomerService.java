package com.drycleaning.system.service;

import com.drycleaning.system.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(Long id);
    Optional<Customer> getCustomerByName(String name);
    Optional<Customer> getCustomerByPhone(String phone);
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customerDetails);
    void deleteCustomer(Long id);
    Customer updateCustomerBalance(Long id, Double newBalance);
}