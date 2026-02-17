package com.drycleaning.system.service.impl;

import com.drycleaning.system.mapper.CustomerMapper;
import com.drycleaning.system.model.Customer;
import com.drycleaning.system.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public List<Customer> getAllCustomers() {
        return customerMapper.selectList(null);
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return Optional.ofNullable(customerMapper.selectById(id));
    }

    @Override
    public Optional<Customer> getCustomerByName(String name) {
        return Optional.ofNullable(customerMapper.findByName(name));
    }

    @Override
    public Optional<Customer> getCustomerByPhone(String phone) {
        return Optional.ofNullable(customerMapper.findByPhone(phone));
    }

    @Override
    public Customer createCustomer(Customer customer) {
        if (customer.getBalance() == null) {
            customer.setBalance(0.0);
        }
        customer.setCreateTime(java.time.LocalDateTime.now().toString());
        customerMapper.insert(customer);
        return customer;
    }

    @Override
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + id);
        }

        customer.setName(customerDetails.getName());
        customer.setPhone(customerDetails.getPhone());
        customer.setWechat(customerDetails.getWechat());
        customer.setBalance(customerDetails.getBalance());

        customerMapper.updateById(customer);
        return customer;
    }

    @Override
    public void deleteCustomer(Long id) {
        customerMapper.deleteById(id);
    }

    @Override
    public Customer updateCustomerBalance(Long id, Double newBalance) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + id);
        }

        customer.setBalance(newBalance);
        customerMapper.updateById(customer);
        return customer;
    }
}
