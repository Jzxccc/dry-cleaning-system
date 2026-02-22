## Purpose

客户管理模块规范 - 定义客户信息创建、存储、搜索和余额管理的功能要求。

## Requirements

### Requirement: Customer Creation
The system SHALL allow staff to create new customers with basic information and optionally an initial recharge amount.

#### Scenario: Successful Customer Creation without Recharge
- **WHEN** staff enters customer details (name, phone) without recharge amount and submits
- **THEN** system creates a new customer record with provided information and balance 0.00

#### Scenario: Successful Customer Creation with Recharge
- **WHEN** staff enters customer details (name, phone) and recharge amount (100 的整数倍) and submits
- **THEN** system creates customer, calculates recharge with bonus (100 送 10%, 200 送 20%), adds total to balance, and creates recharge record

#### Scenario: Customer Creation with Invalid Recharge Amount
- **WHEN** staff enters recharge amount that is not a multiple of 100
- **THEN** system displays error message "充值金额必须是 100 的整数倍"

### Requirement: Customer Information Storage
The system SHALL store customer information including name, phone number, WeChat ID, and balance.

#### Scenario: Customer Data Persistence
- **WHEN** customer information is saved
- **THEN** system stores name, phone, WeChat, and balance in database

### Requirement: Customer Search
The system SHALL allow staff to search for customers by name or phone number.

#### Scenario: Search by Name
- **WHEN** staff enters customer name in search field
- **THEN** system displays matching customer records

#### Scenario: Search by Phone Number
- **WHEN** staff enters customer phone number in search field
- **THEN** system displays matching customer records

### Requirement: Customer Balance Management
The system SHALL track and update customer prepaid balance.

#### Scenario: Balance Update
- **WHEN** customer makes a payment or receives a recharge
- **THEN** system updates the customer's balance accordingly
