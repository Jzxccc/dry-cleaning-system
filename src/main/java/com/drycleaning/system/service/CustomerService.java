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
    
    /**
     * 模糊搜索客户
     * @param name 姓名关键词（可选，支持拼音）
     * @param phone 手机号关键词（可选）
     * @param note 备注关键词（可选）
     * @return 匹配的客户列表
     */
    List<Customer> fuzzySearch(String name, String phone, String note);
    
    /**
     * 按姓名或拼音搜索客户（支持拼音首字母和全拼）
     * @param keyword 姓名、拼音首字母或全拼
     * @return 匹配的客户列表
     */
    List<Customer> searchByNameOrPinyin(String keyword);
    
    /**
     * 模糊搜索客户（支持拼音匹配）
     * @param name 姓名关键词（可选，支持拼音）
     * @param phone 手机号关键词（可选）
     * @param note 备注关键词（可选）
     * @return 匹配的客户列表
     */
    List<Customer> fuzzySearchWithPinyin(String name, String phone, String note);
}