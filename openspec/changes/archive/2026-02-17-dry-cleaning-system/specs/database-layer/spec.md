## ADDED Requirements

### Requirement: SQLite Database Integration
The system SHALL use SQLite as the database for storing all application data.

#### Scenario: Database Connection
- **WHEN** application starts
- **THEN** system establishes connection to SQLite database

### Requirement: Customer Table Structure
The system SHALL maintain a customer table with id, name, phone, wechat, balance, and create_time fields.

#### Scenario: Customer Data Storage
- **WHEN** customer information is saved
- **THEN** system stores data in customer table with proper field mapping

### Requirement: Orders Table Structure
The system SHALL maintain an orders table with id, order_no, customer_id, total_price, prepaid, pay_type, urgent, status, expected_time, and create_time fields.

#### Scenario: Order Data Storage
- **WHEN** order information is saved
- **THEN** system stores data in orders table with proper field mapping

### Requirement: Clothes Table Structure
The system SHALL maintain a clothes table with id, order_id, type, price, damage_remark, damage_image, and status fields.

#### Scenario: Clothes Data Storage
- **WHEN** clothes information is saved
- **THEN** system stores data in clothes table with proper field mapping

### Requirement: Recharge Record Table Structure
The system SHALL maintain a recharge_record table with id, customer_id, recharge_amount, gift_amount, and create_time fields.

#### Scenario: Recharge Record Storage
- **WHEN** recharge transaction is recorded
- **THEN** system stores data in recharge_record table with proper field mapping