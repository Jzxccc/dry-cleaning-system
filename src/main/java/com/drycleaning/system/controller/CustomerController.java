package com.drycleaning.system.controller;

import com.drycleaning.system.model.Customer;
import com.drycleaning.system.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        // 验证必填字段
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("客户姓名不能为空");
        }
        
        Customer createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.ok(createdCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/phone/{phone}")
    public ResponseEntity<Customer> getCustomerByPhone(@PathVariable String phone) {
        Optional<Customer> customer = customerService.getCustomerByPhone(phone);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<Customer> getCustomerByName(@PathVariable String name) {
        Optional<Customer> customer = customerService.getCustomerByName(name);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/balance")
    public ResponseEntity<Customer> updateCustomerBalance(@PathVariable Long id, @RequestParam Double newBalance) {
        Customer updatedCustomer = customerService.updateCustomerBalance(id, newBalance);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * 模糊搜索客户 - 支持姓名、手机号、备注的多条件组合查询（姓名支持拼音）
     * @param name 姓名关键词（可选，支持拼音）
     * @param phone 手机号关键词（可选）
     * @param note 备注关键词（可选）
     * @return 匹配的客户列表
     */
    @GetMapping("/search/fuzzy")
    public ResponseEntity<List<Customer>> fuzzySearchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String note) {
        List<Customer> customers = customerService.fuzzySearchWithPinyin(name, phone, note);
        return ResponseEntity.ok(customers);
    }

    /**
     * 按姓名或拼音搜索客户（支持拼音首字母和全拼）
     * 例如：张三 可以匹配 "张"、"zs"、"zhangsan"
     * @param keyword 姓名、拼音首字母或全拼
     * @return 匹配的客户列表
     */
    @GetMapping("/search/name-or-pinyin")
    public ResponseEntity<List<Customer>> searchByNameOrPinyin(
            @RequestParam String keyword) {
        List<Customer> customers = customerService.searchByNameOrPinyin(keyword);
        return ResponseEntity.ok(customers);
    }
}