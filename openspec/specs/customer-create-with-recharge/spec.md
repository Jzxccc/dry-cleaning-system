## Purpose

创建客户时充值能力规范 - 定义在创建客户账户的同时支持初始充值的功能要求。

## Requirements

### Requirement: Create Customer with Optional Recharge
The system SHALL allow staff to create a new customer and optionally perform an initial recharge in a single operation.

#### Scenario: Create customer without recharge
- **WHEN** staff creates a customer without specifying recharge amount
- **THEN** system creates customer with balance 0.00, no recharge record created

#### Scenario: Create customer with recharge
- **WHEN** staff creates a customer and specifies recharge amount (100 的整数倍)
- **THEN** system creates customer, adds recharge amount + 20% bonus to balance, and creates recharge record

#### Scenario: Create customer with invalid recharge amount
- **WHEN** staff specifies recharge amount that is not a multiple of 100
- **THEN** system rejects the request with appropriate error message

### Requirement: Recharge Calculation
The system SHALL calculate the recharge amount with 20% bonus when creating customer with recharge.

#### Scenario: Recharge calculation example
- **WHEN** staff specifies recharge amount of 100 yuan
- **THEN** system adds 120 yuan to customer balance (100 principal + 20 bonus)

### Requirement: Transaction Atomicity
The system SHALL ensure customer creation and recharge are atomic - both succeed or both fail.

#### Scenario: Transaction rollback on failure
- **WHEN** recharge record creation fails after customer is created
- **THEN** system rolls back the customer creation, no partial data remains

### Requirement: API Backward Compatibility
The system SHALL maintain backward compatibility for existing client code that calls create customer API without recharge.

#### Scenario: Existing API usage unchanged
- **WHEN** existing client calls create customer API without rechargeAmount parameter
- **THEN** system behaves exactly as before, creating customer with balance 0.00
