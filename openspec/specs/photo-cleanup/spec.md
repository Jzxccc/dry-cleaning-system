## Purpose

照片清理功能规范 - 定义订单完成后自动删除衣物照片的功能要求。

## Requirements

### Requirement: Auto Delete Photos on Order Complete
The system SHALL automatically delete clothes photos when an order is completed.

#### Scenario: Order status changed to FINISHED
- **WHEN** staff updates order status to FINISHED
- **THEN** system deletes all photos associated with the order

#### Scenario: Delete photo directory
- **WHEN** photos are deleted
- **THEN** system removes the entire photo directory for that order

#### Scenario: Handle missing photos
- **WHEN** order has no photos
- **THEN** system skips deletion without error

### Requirement: Delete Photos on Order Delete
The system SHALL delete clothes photos when an order is deleted.

#### Scenario: Delete order with photos
- **WHEN** staff deletes an order that has photos
- **THEN** system deletes the photos before deleting the order record
