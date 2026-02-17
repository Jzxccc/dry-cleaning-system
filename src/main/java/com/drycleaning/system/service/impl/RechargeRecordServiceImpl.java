package com.drycleaning.system.service.impl;

import com.drycleaning.system.mapper.RechargeRecordMapper;
import com.drycleaning.system.model.RechargeRecord;
import com.drycleaning.system.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public List<RechargeRecord> getAllRechargeRecords() {
        return rechargeRecordMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>());
    }

    @Override
    public Optional<RechargeRecord> getRechargeRecordById(Long id) {
        return Optional.ofNullable(rechargeRecordMapper.selectById(id));
    }

    @Override
    public List<RechargeRecord> getRechargeRecordsByCustomerId(Long customerId) {
        return rechargeRecordMapper.findByCustomerId(customerId);
    }

    @Override
    public RechargeRecord createRechargeRecord(RechargeRecord rechargeRecord) {
        Double rechargeAmount = rechargeRecord.getRechargeAmount();
        Double giftAmount = rechargeAmount * 0.2;
        rechargeRecord.setGiftAmount(giftAmount);
        rechargeRecord.setCreateTime(java.time.LocalDateTime.now().toString());
        rechargeRecordMapper.insert(rechargeRecord);
        return rechargeRecord;
    }

    @Override
    public void deleteRechargeRecord(Long id) {
        rechargeRecordMapper.deleteById(id);
    }
}
