## Purpose

图片放大查看能力规范 - 定义用户点击衣物照片缩略图后放大查看的功能要求。

## Requirements

### Requirement: Zoom Photo on Tap
The system SHALL display a full-size photo dialog when staff taps on a photo thumbnail.

#### Scenario: Tap photo thumbnail
- **WHEN** staff taps on a photo thumbnail in order detail page
- **THEN** system opens a full-screen dialog displaying the enlarged photo

### Requirement: Pinch to Zoom
The system SHALL allow staff to zoom in/out using pinch gesture.

#### Scenario: Pinch to zoom in
- **WHEN** staff performs pinch-out gesture on the photo
- **THEN** system enlarges the photo proportionally

#### Scenario: Pinch to zoom out
- **WHEN** staff performs pinch-in gesture on the photo
- **THEN** system reduces the photo size

#### Scenario: Reset zoom on close
- **WHEN** staff closes the dialog
- **THEN** system resets zoom level to default

### Requirement: Swipe to Navigate Photos
The system SHALL allow staff to swipe left/right to navigate between multiple photos.

#### Scenario: Swipe to next photo
- **WHEN** staff swipes left on the photo
- **THEN** system displays the next photo in the list

#### Scenario: Swipe to previous photo
- **WHEN** staff swipes right on the photo
- **THEN** system displays the previous photo in the list

#### Scenario: Single photo navigation hidden
- **WHEN** only one photo exists
- **THEN** system hides navigation indicators

### Requirement: Close Dialog
The system SHALL provide multiple ways to close the photo dialog.

#### Scenario: Close with back button
- **WHEN** staff presses device back button
- **THEN** system closes the dialog

#### Scenario: Close with close button
- **WHEN** staff taps the close button (X)
- **THEN** system closes the dialog

#### Scenario: Close by tapping outside
- **WHEN** staff taps outside the dialog area
- **THEN** system closes the dialog
