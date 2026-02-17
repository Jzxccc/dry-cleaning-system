## Purpose

统计功能规范 - 定义收入统计和未完成订单计数的功能要求。

## Requirements

### Requirement: Daily Income Statistics
The system SHALL provide statistics on daily income from orders.

#### Scenario: Daily Income Calculation
- **WHEN** staff views daily statistics
- **THEN** system displays total income for the current day

### Requirement: Monthly Income Statistics
The system SHALL provide statistics on monthly income from orders.

#### Scenario: Monthly Income Calculation
- **WHEN** staff views monthly statistics
- **THEN** system displays total income for the current month

### Requirement: Payment Method Statistics
The system SHALL provide statistics on income by payment method (cash vs prepaid).

#### Scenario: Cash Income Calculation
- **WHEN** staff views payment method statistics
- **THEN** system displays total income from cash payments

#### Scenario: Prepaid Income Calculation
- **WHEN** staff views payment method statistics
- **THEN** system displays total income from prepaid payments

### Requirement: Unfinished Orders Count
The system SHALL provide count of orders that are not yet finished.

#### Scenario: Unfinished Orders Count
- **WHEN** staff views statistics
- **THEN** system displays count of orders with status UNWASHED or WASHED
