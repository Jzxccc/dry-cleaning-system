## ADDED Requirements

### Requirement: Clothes Addition to Order
The system SHALL allow staff to add clothes to an existing order.

#### Scenario: Adding Clothes Successfully
- **WHEN** staff selects clothes type and quantity for an order
- **THEN** system adds the clothes to the order with default prices

### Requirement: Clothes Information Storage
The system SHALL store clothes information including order ID, type, price, damage remarks, and status.

#### Scenario: Clothes Data Persistence
- **WHEN** clothes are added to an order
- **THEN** system stores all required information in database

### Requirement: Clothes Price Modification
The system SHALL allow staff to modify the price of individual clothes items.

#### Scenario: Price Modification
- **WHEN** staff modifies the price of a clothes item
- **THEN** system updates the price and recalculates the order total

### Requirement: Damage Documentation
The system SHALL allow staff to document damage remarks and upload damage images for clothes.

#### Scenario: Damage Documentation
- **WHEN** staff adds damage remarks or uploads damage images
- **THEN** system stores the damage information with the clothes record