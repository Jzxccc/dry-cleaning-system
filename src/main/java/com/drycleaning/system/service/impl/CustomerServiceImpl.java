package com.drycleaning.system.service.impl;

import com.drycleaning.system.mapper.CustomerMapper;
import com.drycleaning.system.mapper.RechargeRecordMapper;
import com.drycleaning.system.model.Customer;
import com.drycleaning.system.model.RechargeRecord;
import com.drycleaning.system.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

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
        // 获取所有客户
        List<Customer> allCustomers = customerMapper.selectList(null);
        
        // 过滤条件
        return allCustomers.stream()
            .filter(customer -> {
                // 手机号过滤
                if (phone != null && !phone.trim().isEmpty()) {
                    if (customer.getPhone() == null || !customer.getPhone().contains(phone.trim())) {
                        return false;
                    }
                }
                
                // 备注过滤（Customer 模型没有 note 字段，跳过）
                
                // 姓名过滤（支持拼音）
                if (name != null && !name.trim().isEmpty()) {
                    if (customer.getName() == null) {
                        return false;
                    }
                    String trimmedName = name.trim();
                    // 姓名包含匹配
                    boolean nameMatches = customer.getName().contains(trimmedName);
                    // 拼音匹配
                    boolean pinyinMatches = com.drycleaning.system.util.PinyinUtil.matchesPinyin(
                        customer.getName(), trimmedName);
                    
                    if (!nameMatches && !pinyinMatches) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public Customer createCustomerWithRecharge(Customer customer, Double rechargeAmount) {
        // 验证充值金额：如果为 null 或 0，则只创建客户不充值
        if (rechargeAmount == null || rechargeAmount <= 0) {
            return createCustomer(customer);
        }

        // 验证充值金额必须是 100 的整数倍
        if (rechargeAmount % 100 != 0) {
            throw new IllegalArgumentException("充值金额必须是 100 的整数倍");
        }

        // 创建客户（初始余额为 0）
        customer.setBalance(0.0);
        customer.setCreateTime(java.time.LocalDateTime.now().toString());
        customerMapper.insert(customer);

        // 计算赠送金额（阶梯比例：100 送 10%，200 送 20%）
        Double giftAmount;
        if (rechargeAmount >= 200) {
            giftAmount = rechargeAmount * 0.2;
        } else if (rechargeAmount >= 100) {
            giftAmount = rechargeAmount * 0.1;
        } else {
            giftAmount = 0.0;
        }
        Double totalAmount = rechargeAmount + giftAmount;

        // 更新客户余额
        customer.setBalance(totalAmount);
        customerMapper.updateById(customer);

        // 创建充值记录
        RechargeRecord record = new RechargeRecord();
        record.setCustomerId(customer.getId());
        record.setRechargeAmount(rechargeAmount);
        record.setGiftAmount(giftAmount);
        rechargeRecordMapper.insert(record);

        return customer;
    }
}
