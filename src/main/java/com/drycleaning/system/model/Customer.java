package com.drycleaning.system.model;

import com.baomidou.mybatisplus.annotation.*;

@TableName("customer")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("phone")
    private String phone;

    @TableField("wechat")
    private String wechat;

    @TableField("balance")
    private Double balance;

    @TableField("create_time")
    private String createTime;

    // Constructors
    public Customer() {
    }

    public Customer(String name, String phone, String wechat, Double balance) {
        this.name = name;
        this.phone = phone;
        this.wechat = wechat;
        this.balance = balance;
        this.createTime = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
