package com.drycleaning.system.model;

import com.baomidou.mybatisplus.annotation.*;

@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("customer_id")
    private Long customerId;

    @TableField("total_price")
    private Double totalPrice;

    @TableField("prepaid")
    private Double prepaid;

    @TableField("pay_type")
    private String payType;

    @TableField("urgent")
    private Integer urgent;

    @TableField("status")
    private String status;

    @TableField("expected_time")
    private String expectedTime;

    @TableField("create_time")
    private String createTime;

    // Constructors
    public Order() {
    }

    public Order(String orderNo, Long customerId, Double totalPrice, Double prepaid, String payType, Integer urgent, String status, String expectedTime) {
        this.orderNo = orderNo;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
        this.prepaid = prepaid;
        this.payType = payType;
        this.urgent = urgent;
        this.status = status;
        this.expectedTime = expectedTime;
        this.createTime = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getPrepaid() {
        return prepaid;
    }

    public void setPrepaid(Double prepaid) {
        this.prepaid = prepaid;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Integer getUrgent() {
        return urgent;
    }

    public void setUrgent(Integer urgent) {
        this.urgent = urgent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
