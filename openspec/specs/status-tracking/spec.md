## Purpose

状态跟踪规范 - 定义衣物状态流转和一致性的功能要求。

## Requirements

### Requirement: Status Tracking
The system SHALL track the status of clothes through three stages: UNWASHED, WASHED, FINISHED.

#### Scenario: Initial Status Assignment
- **WHEN** a new order with clothes is created
- **THEN** system assigns UNWASHED status to all clothes in the order

#### Scenario: Status Transition from UNWASHED to WASHED
- **WHEN** staff marks clothes as washed
- **THEN** system updates status from UNWASHED to WASHED

#### Scenario: Status Transition from WASHED to FINISHED
- **WHEN** customer picks up the clothes
- **THEN** system updates status from WASHED to FINISHED

### Requirement: Status Consistency
The system SHALL ensure that all clothes in an order maintain consistent status transitions.

#### Scenario: Bulk Status Update
- **WHEN** staff updates status for an entire order
- **THEN** system updates status for all clothes in that order
