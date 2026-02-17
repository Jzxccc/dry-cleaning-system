package com.drycleaning.system.controller;

import com.drycleaning.system.model.Customer;
import com.drycleaning.system.model.RechargeRecord;
import com.drycleaning.system.service.CustomerService;
import com.drycleaning.system.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prepaid")
public class PrepaidController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    // 充值功能（含 20% 赠送）
    @PostMapping("/recharge")
    public ResponseEntity<String> recharge(@RequestParam Long customerId, @RequestParam Double amount) {
        Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
        if (!customerOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Customer not found");
        }

        Customer customer = customerOpt.get();
        
        // 计算赠送金额
        Double giftAmount = amount * 0.2;
        Double totalAdded = amount + giftAmount;
        
        // 更新客户余额
        Double newBalance = customer.getBalance() + totalAdded;
        customerService.updateCustomerBalance(customerId, newBalance);

        // 创建充值记录
        RechargeRecord record = new RechargeRecord();
        record.setCustomerId(customerId);
        record.setRechargeAmount(amount);
        record.setGiftAmount(giftAmount);
        
        rechargeRecordService.createRechargeRecord(record);

        return ResponseEntity.ok("Recharge successful. Amount: " + amount + ", Gift: " + giftAmount + ", New Balance: " + newBalance);
    }

    // 预付费支付功能
    @PostMapping("/pay")
    public ResponseEntity<String> payWithPrepaid(@RequestParam Long customerId, @RequestParam Double amount) {
        Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
        if (!customerOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Customer not found");
        }

        Customer customer = customerOpt.get();
        
        if (customer.getBalance() < amount) {
            return ResponseEntity.badRequest().body("Insufficient balance. Current balance: " + customer.getBalance());
        }

        // 扣除余额
        Double newBalance = customer.getBalance() - amount;
        customerService.updateCustomerBalance(customerId, newBalance);

        return ResponseEntity.ok("Payment successful. Deducted: " + amount + ", Remaining balance: " + newBalance);
    }

    // 获取充值记录
    @GetMapping("/recharge-records/{customerId}")
    public ResponseEntity<List<RechargeRecord>> getRechargeRecords(@PathVariable Long customerId) {
        List<RechargeRecord> records = rechargeRecordService.getRechargeRecordsByCustomerId(customerId);
        return ResponseEntity.ok(records);
    }
}
