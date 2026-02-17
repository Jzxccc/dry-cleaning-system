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

    @Override
    public List<Customer> fuzzySearch(String name, String phone, String note) {
        return customerMapper.fuzzySearch(name, phone, note);
    }

    @Override
    public List<Customer> searchByNameOrPinyin(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        
        String trimmedKeyword = keyword.trim();
        List<Customer> allCustomers = getAllCustomers();
        
        // 过滤匹配的客户
        return allCustomers.stream()
            .filter(customer -> {
                if (customer.getName() == null) {
                    return false;
                }
                // 姓名包含匹配
                if (customer.getName().contains(trimmedKeyword)) {
                    return true;
                }
                // 拼音匹配
                return com.drycleaning.system.util.PinyinUtil.matchesPinyin(
                    customer.getName(), trimmedKeyword);
            })
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Customer> fuzzySearchWithPinyin(String name, String phone, String note) {
        List<Customer> results = customerMapper.fuzzySearch(null, phone, note);
        
        // 如果姓名为空，直接返回结果
        if (name == null || name.trim().isEmpty()) {
            return results;
        }
        
        String trimmedName = name.trim();
        
        // 过滤姓名匹配（包含拼音匹配）
        return results.stream()
            .filter(customer -> {
                if (customer.getName() == null) {
                    return false;
                }
                // 姓名包含匹配
                if (customer.getName().contains(trimmedName)) {
                    return true;
                }
                // 拼音匹配
                return com.drycleaning.system.util.PinyinUtil.matchesPinyin(
                    customer.getName(), trimmedName);
            })
            .collect(java.util.stream.Collectors.toList());
    }
}
