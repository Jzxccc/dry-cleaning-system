## Purpose

页面布局系统规范 - 定义主要页面的优化布局和视觉呈现要求。

## ADDED Requirements

### Requirement: Home Page Layout
The system SHALL provide an optimized home page layout with clear navigation and quick actions.

#### Scenario: Home Page Header
- **WHEN** user opens the application
- **THEN** system displays header with app title and current date in large, prominent style

#### Scenario: Quick Actions Grid
- **WHEN** user views home page
- **THEN** system displays quick action buttons in a grid layout with adequate spacing (16dp)

#### Scenario: Navigation Menu
- **WHEN** user accesses navigation
- **THEN** system displays navigation drawer with clear categories and icons

### Requirement: Customer List Page Layout
The system SHALL provide an optimized customer list page with searchable and scrollable layout.

#### Scenario: Search Bar Placement
- **WHEN** user views customer list page
- **THEN** system displays search bar at top with full width and prominent styling

#### Scenario: Customer Card List
- **WHEN** user scrolls through customer list
- **THEN** system displays customer cards with consistent spacing (8dp) and clear information hierarchy

#### Scenario: Empty State Display
- **WHEN** no customers match search criteria
- **THEN** system displays empty state with helpful message and action button

### Requirement: Order List Page Layout
The system SHALL provide an optimized order list page with filtering and sorting capabilities.

#### Scenario: Filter Chips
- **WHEN** user views order list page
- **THEN** system displays filter chips for order status (UNWASHED, WASHED, FINISHED)

#### Scenario: Order Card Display
- **WHEN** user views order list
- **THEN** system displays order cards with status indicator, customer info, and timestamp

#### Scenario: Status Badge
- **WHEN** order status is displayed
- **THEN** system shows colored badge corresponding to order status (blue, green, gray)

### Requirement: Order Detail Page Layout
The system SHALL provide an optimized order detail page with clear information hierarchy.

#### Scenario: Order Header Section
- **WHEN** user views order detail
- **THEN** system displays order number, customer name, and status prominently at top

#### Scenario: Order Items List
- **WHEN** user views order contents
- **THEN** system displays clothing items in a list with quantity and price details

#### Scenario: Action Buttons Layout
- **WHEN** user needs to perform order actions
- **THEN** system displays action buttons (Update Status, Payment) at bottom in fixed position

### Requirement: Responsive Layout Support
The system SHALL provide responsive layouts that adapt to different screen sizes.

#### Scenario: Tablet Landscape Mode
- **WHEN** device is in landscape orientation
- **THEN** system adjusts layout to utilize horizontal space efficiently

#### Scenario: Minimum Touch Target
- **WHEN** user interacts with any clickable element
- **THEN** system ensures minimum touch target size of 48dp x 48dp
