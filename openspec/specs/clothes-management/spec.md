## Purpose

衣物管理模块规范 - 定义衣物添加到订单、存储、价格修改、损坏记录和拍照参考的功能要求。

## Requirements

### Requirement: Clothes Addition to Order
The system SHALL allow staff to add clothes to an existing order.

#### Scenario: Adding Clothes Successfully
- **WHEN** staff selects clothes type and quantity for an order
- **THEN** system adds the clothes to the order with default prices

### Requirement: Clothes Information Storage
The system SHALL store clothes information including order ID, type, price, damage remarks, and status.

#### Scenario: Clothes Data Persistence
- **WHEN** clothes are added to an order
- **THEN** system stores all required information in database

### Requirement: Clothes Price Modification
The system SHALL allow staff to modify the price of individual clothes items.

#### Scenario: Price Modification
- **WHEN** staff modifies the price of a clothes item
- **THEN** system updates the price and recalculates the order total

### Requirement: Damage Documentation
The system SHALL allow staff to document damage remarks and upload damage images for clothes.

#### Scenario: Damage Documentation
- **WHEN** staff adds damage remarks or uploads damage images
- **THEN** system stores the damage information with the clothes record

### Requirement: Photo Reference for Clothes
The system SHALL allow photos to be taken as reference for clothes condition, not requiring exact one-to-one correspondence.

#### Scenario: Take general reference photos
- **WHEN** staff adds multiple clothes items
- **THEN** system allows taking photos that show the general condition of all clothes

#### Scenario: Photos for reference only
- **WHEN** photos are taken
- **THEN** system treats photos as general reference, not requiring exact match to specific clothing items

### Requirement: Order Creation
The system SHALL allow staff to create new orders linked to customers with optional clothes photos.

#### Scenario: Successful Order Creation
- **WHEN** staff selects a customer and adds order details with optional photos
- **THEN** system creates a new order with unique order number and stores photo paths
