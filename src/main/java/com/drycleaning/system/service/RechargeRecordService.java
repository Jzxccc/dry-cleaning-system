package com.drycleaning.system.service;

import com.drycleaning.system.model.RechargeRecord;

import java.util.List;
import java.util.Optional;

public interface RechargeRecordService {
    List<RechargeRecord> getAllRechargeRecords();
    Optional<RechargeRecord> getRechargeRecordById(Long id);
    List<RechargeRecord> getRechargeRecordsByCustomerId(Long customerId);
    RechargeRecord createRechargeRecord(RechargeRecord rechargeRecord);
    void deleteRechargeRecord(Long id);
}