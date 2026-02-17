package com.drycleaning.system.model;

import com.baomidou.mybatisplus.annotation.*;

@TableName("recharge_record")
public class RechargeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("customer_id")
    private Long customerId;

    @TableField("recharge_amount")
    private Double rechargeAmount;

    @TableField("gift_amount")
    private Double giftAmount;

    @TableField("create_time")
    private String createTime;

    // Constructors
    public RechargeRecord() {
    }

    public RechargeRecord(Long customerId, Double rechargeAmount, Double giftAmount) {
        this.customerId = customerId;
        this.rechargeAmount = rechargeAmount;
        this.giftAmount = giftAmount;
        this.createTime = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Double getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(Double giftAmount) {
        this.giftAmount = giftAmount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
