package com.drycleaning.system.enums;

public enum ClothesStatus {
    UNWASHED("未洗"),
    WASHED("已洗"),
    FINISHED("已取");

    private final String description;

    ClothesStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}