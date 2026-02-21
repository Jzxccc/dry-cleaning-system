## Purpose

衣物管理模块规范 - 定义订单创建时衣物拍照的功能要求（修改版）。

## ADDED Requirements

### Requirement: Photo Reference for Clothes
The system SHALL allow photos to be taken as reference for clothes condition, not requiring exact one-to-one correspondence.

#### Scenario: Take general reference photos
- **WHEN** staff adds multiple clothes items
- **THEN** system allows taking photos that show the general condition of all clothes

#### Scenario: Photos for reference only
- **WHEN** photos are taken
- **THEN** system treats photos as general reference, not requiring exact match to specific clothing items

## MODIFIED Requirements

### Requirement: Order Creation
The system SHALL allow staff to create new orders linked to customers with optional clothes photos.

#### Scenario: Successful Order Creation
- **WHEN** staff selects a customer and adds order details with optional photos
- **THEN** system creates a new order with unique order number and stores photo paths
