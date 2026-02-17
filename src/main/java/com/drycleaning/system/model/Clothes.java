package com.drycleaning.system.model;

import com.baomidou.mybatisplus.annotation.*;

@TableName("clothes")
public class Clothes {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private String orderId;

    @TableField("type")
    private String type;

    @TableField("price")
    private Double price;

    @TableField("damage_remark")
    private String damageRemark;

    @TableField("damage_image")
    private String damageImage;

    @TableField("status")
    private String status;

    @TableField("create_time")
    private String createTime;

    // Constructors
    public Clothes() {
    }

    public Clothes(String orderId, String type, Double price, String damageRemark, String damageImage, String status) {
        this.orderId = orderId;
        this.type = type;
        this.price = price;
        this.damageRemark = damageRemark;
        this.damageImage = damageImage;
        this.status = status;
        this.createTime = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDamageRemark() {
        return damageRemark;
    }

    public void setDamageRemark(String damageRemark) {
        this.damageRemark = damageRemark;
    }

    public String getDamageImage() {
        return damageImage;
    }

    public void setDamageImage(String damageImage) {
        this.damageImage = damageImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
