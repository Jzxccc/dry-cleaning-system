## ADDED Requirements

### Requirement: Prepaid Recharge
The system SHALL allow customers to recharge their prepaid account with a bonus percentage.

#### Scenario: Successful Recharge
- **WHEN** staff processes a recharge of amount X
- **THEN** system adds X + (X * 0.2) to customer's balance (20% bonus)

### Requirement: Payment Processing
The system SHALL allow customers to pay for orders using their prepaid balance.

#### Scenario: Payment with Prepaid Balance
- **WHEN** customer selects prepaid payment option and has sufficient balance
- **THEN** system deducts the order amount from customer's balance

#### Scenario: Insufficient Balance Warning
- **WHEN** customer tries to pay with insufficient prepaid balance
- **THEN** system displays warning and suggests alternative payment methods

### Requirement: Recharge Record Keeping
The system SHALL maintain records of all recharge transactions including amounts and timestamps.

#### Scenario: Recharge Record Creation
- **WHEN** a recharge transaction occurs
- **THEN** system creates a record with customer ID, recharge amount, gift amount, and timestamp