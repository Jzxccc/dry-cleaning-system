## Purpose

衣物拍照功能规范 - 定义在创建订单时为衣物拍照的功能要求。

## Requirements

### Requirement: Take Photo for Clothes
The system SHALL allow staff to take photos for clothes when creating an order.

#### Scenario: Take photo after adding clothes
- **WHEN** staff adds clothes to the order
- **THEN** system displays a camera button below the clothes list

#### Scenario: Capture photo
- **WHEN** staff taps the camera button
- **THEN** system opens camera interface for photo capture

#### Scenario: Save photo as thumbnail
- **WHEN** photo is captured
- **THEN** system saves the photo as a thumbnail (max 200x200 pixels)

#### Scenario: Store photo in app private directory
- **WHEN** photo is saved
- **THEN** system stores the photo in app private directory under `/photos/order_{orderId}/`

### Requirement: View Clothes Photos
The system SHALL allow staff to view taken photos for the current order.

#### Scenario: View photo thumbnails
- **WHEN** staff views the order creation page
- **THEN** system displays thumbnails of all taken photos

#### Scenario: View full size photo
- **WHEN** staff taps on a photo thumbnail
- **THEN** system displays the photo in full size dialog

### Requirement: Request Camera Permission
The system SHALL request camera permission from user before taking photos.

#### Scenario: First time camera access
- **WHEN** staff taps camera button for the first time
- **THEN** system requests camera permission from Android

#### Scenario: Permission denied
- **WHEN** staff denies camera permission
- **THEN** system shows a message explaining why photo is needed and allows to skip
