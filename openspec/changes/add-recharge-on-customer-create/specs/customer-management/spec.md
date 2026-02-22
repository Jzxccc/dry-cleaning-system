## MODIFIED Requirements

### Requirement: Customer Creation
The system SHALL allow staff to create new customers with basic information and optionally an initial recharge amount.

#### Scenario: Successful Customer Creation without Recharge
- **WHEN** staff enters customer details (name, phone) without recharge amount and submits
- **THEN** system creates a new customer record with provided information and balance 0.00

#### Scenario: Successful Customer Creation with Recharge
- **WHEN** staff enters customer details (name, phone) and recharge amount (100 的整数倍) and submits
- **THEN** system creates customer, calculates recharge with 20% bonus, adds total to balance, and creates recharge record

#### Scenario: Customer Creation with Invalid Recharge Amount
- **WHEN** staff enters recharge amount that is not a multiple of 100
- **THEN** system displays error message "充值金额必须是 100 的整数倍"
