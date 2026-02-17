## Purpose

订单管理模块规范 - 定义订单创建、存储、搜索和状态跟踪的功能要求。

## Requirements

### Requirement: Order Creation
The system SHALL allow staff to create new orders linked to customers.

#### Scenario: Successful Order Creation
- **WHEN** staff selects a customer and adds order details
- **THEN** system creates a new order with unique order number

### Requirement: Order Information Storage
The system SHALL store order information including customer ID, total price, payment type, status, and creation time.

#### Scenario: Order Data Persistence
- **WHEN** order is created
- **THEN** system stores all required information in database

### Requirement: Order Search
The system SHALL allow staff to search for orders by customer name or phone number.

#### Scenario: Search Orders by Customer
- **WHEN** staff enters customer name or phone number
- **THEN** system displays all orders associated with that customer

### Requirement: Order Status Tracking
The system SHALL track the status of orders (UNWASHED, WASHED, FINISHED).

#### Scenario: Order Status Update
- **WHEN** order status changes
- **THEN** system updates the status in database and reflects the change in UI
