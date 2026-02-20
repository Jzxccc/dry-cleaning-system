## Purpose

Material Design 3 主题系统规范 - 定义 Android 客户端的颜色、字体、形状等设计令牌。

## ADDED Requirements

### Requirement: Material Design 3 Color System
The system SHALL implement Material Design 3 color system with primary, secondary, and neutral color palettes.

#### Scenario: Primary Color Application
- **WHEN** user views any screen in the application
- **THEN** system displays primary color (#2196F3) for main actions and key UI elements

#### Scenario: Secondary Color Usage
- **WHEN** user interacts with secondary actions or accent elements
- **THEN** system displays secondary color (#03DAC6) for emphasis and differentiation

#### Scenario: Dark Theme Support
- **WHEN** system dark mode is enabled
- **THEN** system applies dark color palette with appropriate surface and background colors

### Requirement: Typography Scale
The system SHALL use Material Design 3 typography scale with consistent font sizes and weights.

#### Scenario: Display Text
- **WHEN** user views headings and titles
- **THEN** system displays text using Display Large/Medium/Small styles (57sp/45sp/36sp)

#### Scenario: Body Text
- **WHEN** user reads content and descriptions
- **THEN** system displays text using Body Large/Medium/Small styles (16sp/14sp/12sp) with appropriate line height

#### Scenario: Button Text
- **WHEN** user views button labels
- **THEN** system displays text using Label Large style (14sp, medium weight)

### Requirement: Shape System
The system SHALL use Material Design 3 shape tokens for consistent corner rounding.

#### Scenario: Small Components
- **WHEN** user views checkboxes, small icons
- **THEN** system applies small shape (4dp corner radius)

#### Scenario: Medium Components
- **WHEN** user views cards, buttons
- **THEN** system applies medium shape (8dp-12dp corner radius)

#### Scenario: Large Components
- **WHEN** user views modal dialogs, large containers
- **THEN** system applies large shape (16dp-28dp corner radius)

### Requirement: Elevation and Shadow
The system SHALL use Material Design 3 elevation system for visual depth.

#### Scenario: Card Elevation
- **WHEN** user views card components
- **THEN** system displays appropriate shadow based on elevation level (1dp-6dp)

#### Scenario: Interactive Elevation
- **WHEN** user presses a button
- **THEN** system reduces elevation to provide tactile feedback
