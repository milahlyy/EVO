# EVO

## Event Vendor Operations

Version: 2.0  
Platform: Java 17, Java Swing, JSON File Storage, Gson JAR

---

# 1. Project Overview

## 1.1 Background

Event Organizer businesses manage many operational details, including clients, event schedules, vendors, payments, and reports. Manual management can cause duplicated records, missed assignments, and inconsistent payment tracking.

EVO (Event Vendor Operations) is a desktop-based management system designed to centralize event operations while demonstrating Object-Oriented Programming concepts. This project uses local JSON files for persistence because the university project focus is OOP design, exception handling, and GUI implementation.

## 1.2 Objectives

- Manage user authentication and roles.
- Manage client information.
- Manage event planning and event details.
- Manage vendor data and vendor assignments.
- Process payments and generate invoices.
- Generate operational and revenue reports.
- Demonstrate Object-Oriented Programming principles clearly.
- Persist application data using local JSON files.

## 1.3 Scope

This application is intended for internal Event Organizer operations. It does not include public online booking, external payment gateways, networking, or server-based persistence.

---

# 2. Stakeholders

## Admin

- Full system access
- Manage users
- Access all reports

## Staff

- Manage clients
- Manage events
- Manage vendors
- Manage payments
- View operational reports

---

# 3. Technology Stack

- Java 17
- Java Swing
- JSON File Storage
- Gson for JSON serialization and deserialization
- Git and GitHub for version control

## 3.1 Gson Dependency

Gson is used to convert Java objects to JSON and JSON back to Java objects. The project uses a local Gson `.jar` file stored in the `lib/` directory.

Required library path:

```text
lib/gson-2.10.1.jar
```

VS Code reads the library through `.vscode/settings.json`.

---

# 4. Architecture

## 4.1 Layered Architecture

```text
GUI -> Service -> Storage -> JSON Files
```

## 4.2 Layer Responsibilities

## GUI Layer

- Displays forms, tables, dialogs, and navigation.
- Captures user input.
- Calls service classes.
- Does not access JSON files directly.
- Does not contain persistence logic.

## Service Layer

- Handles business logic.
- Handles validation.
- Coordinates model and storage classes.
- Throws custom exceptions when rules are violated.
- Calls storage classes for create, read, update, and delete operations.

## Storage Layer

- Reads data from JSON files.
- Writes data to JSON files.
- Serializes and deserializes model objects using Gson.
- Handles file I/O exceptions.
- Does not contain business logic.

---

# 5. Local File Storage

## 5.1 Data Directory

Persistent data is stored in local JSON files:

```text
data/
|-- users.json
|-- clients.json
|-- events.json
|-- vendors.json
`-- payments.json
```

## 5.2 Persistence Rules

- Each module loads data from JSON on startup.
- Each module saves data back to JSON after create, update, or delete operations.
- JSON files store arrays of objects.
- Storage classes must create missing files with an empty array.
- Storage classes must use try-catch for file operations.
- Service classes must not perform file I/O.
- GUI classes must not perform file I/O.

## 5.3 Storage Classes

```text
src/storage/
|-- JsonStorage.java
|-- UserStorage.java
|-- ClientStorage.java
|-- EventStorage.java
|-- VendorStorage.java
`-- PaymentStorage.java
```

## 5.4 Storage Responsibilities

## JsonStorage

- Provides reusable JSON read/write behavior.
- Uses Gson.
- Handles file creation when a data file does not exist.

## UserStorage

- Reads and writes `users.json`.
- Persists `User`, `Admin`, and `Staff` data.

## ClientStorage

- Reads and writes `clients.json`.
- Persists client records.

## EventStorage

- Reads and writes `events.json`.
- Persists `WeddingEvent`, `SeminarEvent`, and `BirthdayEvent` data.

## VendorStorage

- Reads and writes `vendors.json`.
- Persists `CateringVendor`, `DecorationVendor`, and `PhotographyVendor` data.

## PaymentStorage

- Reads and writes `payments.json`.
- Persists `CashPayment`, `TransferPayment`, and `EWalletPayment` data.

---

# 6. Functional Requirements

## FR-01 Authentication and User Management

### Features

- Login
- Logout
- Session management
- Admin and staff role handling
- Add, update, and remove users

### Classes

- `User`
- `Admin`
- `Staff`
- `UserService`
- `UserStorage`

### OOP Concepts

- Encapsulation through private user fields.
- Inheritance through `Admin` and `Staff`.
- Polymorphism through role-specific behavior.

---

## FR-02 Client Management

### Features

- Add client
- Update client
- Delete client
- Search client
- View client details

### Validation

- Required fields validation
- Email validation
- Phone number validation

### Classes

- `Client`
- `ClientService`
- `ClientStorage`
- `ValidationException`

---

## FR-03 Event Management

### Features

- Create event
- Update event
- Delete event
- View event details
- Assign client to event
- Calculate event budget

### Event Types

## WeddingEvent

- Package
- Catering capacity
- Decoration option

## SeminarEvent

- Topic
- Speaker count
- Participant count

## BirthdayEvent

- Theme
- Entertainment option
- Guest count

### Classes

- `Event` abstract class
- `WeddingEvent`
- `SeminarEvent`
- `BirthdayEvent`
- `EventService`
- `EventStorage`
- `EventException`

### OOP Concepts

- Abstraction through `abstract Event`.
- Inheritance through event subclasses.
- Polymorphism through `calculateBudget()`.

---

## FR-04 Vendor Management

### Features

- Add vendor
- Update vendor
- Delete vendor
- Search vendor
- Assign vendor to event
- Remove vendor assignment

### Vendor Types

- `CateringVendor`
- `DecorationVendor`
- `PhotographyVendor`

### Classes

- `Vendor`
- `CateringVendor`
- `DecorationVendor`
- `PhotographyVendor`
- `VendorService`
- `VendorStorage`

### OOP Concepts

- Encapsulation through private vendor fields.
- Inheritance through vendor subclasses.
- Polymorphism through vendor-specific pricing or service descriptions.

---

## FR-05 Payment and Reporting

### Features

- Record payment
- Process payment
- Generate invoice
- View payment history
- Generate revenue report
- Generate event report

### Payment Types

- `CashPayment`
- `TransferPayment`
- `EWalletPayment`

### Classes

- `Payment` abstract class
- `CashPayment`
- `TransferPayment`
- `EWalletPayment`
- `PaymentService`
- `PaymentStorage`
- `PaymentException`

### OOP Concepts

- Abstraction through `abstract Payment`.
- Inheritance through payment subclasses.
- Polymorphism through `processPayment()` and `generateInvoice()`.

---

# 7. Non-Functional Requirements

## Usability

- GUI should be simple, readable, and suitable for desktop use.
- Forms should show clear validation messages.
- Tables should support viewing and selecting records.

## Reliability

- Invalid input must be rejected with custom exceptions.
- Missing JSON files must be recreated safely.
- File read/write failures must be handled gracefully.

## Maintainability

- Follow package separation.
- Keep business logic out of GUI classes.
- Keep file I/O out of service classes.
- Use clear class names and method names.

---

# 8. Package Structure

```text
src/
|-- model/
|-- service/
|-- storage/
|-- gui/
|-- util/
|-- exception/
`-- main/
```

## 8.1 Package Purpose

- `model`: Entity classes and OOP hierarchy.
- `service`: Business logic and validation.
- `storage`: JSON persistence.
- `gui`: Java Swing screens.
- `util`: Shared helper classes.
- `exception`: Custom exception classes.
- `main`: Application entry point.

---

# 9. Team Responsibilities

Each developer must implement model classes, service classes, storage classes, GUI screens, and custom exceptions for their module.

## Developer 1: Authentication and User Management

- `User`, `Admin`, `Staff`
- `UserService`
- `UserStorage`
- Login and user management screens
- Authentication-related validation

## Developer 2: Client Management

- `Client`
- `ClientService`
- `ClientStorage`
- Client management screens
- `ValidationException` usage

## Developer 3: Event Management

- `Event`, `WeddingEvent`, `SeminarEvent`, `BirthdayEvent`
- `EventService`
- `EventStorage`
- Event management screens
- `EventException`

## Developer 4: Vendor Management

- `Vendor`, `CateringVendor`, `DecorationVendor`, `PhotographyVendor`
- `VendorService`
- `VendorStorage`
- Vendor management screens
- Vendor assignment validation

## Developer 5: Payment and Reporting

- `Payment`, `CashPayment`, `TransferPayment`, `EWalletPayment`
- `PaymentService`
- `PaymentStorage`
- Payment and report screens
- `PaymentException`

---

# 10. Work Breakdown Structure

## Phase 1 - Planning

- Requirements gathering
- PRD update
- Team assignment
- Module responsibility mapping

## Phase 2 - Design

- Class diagram
- GUI design
- JSON structure design
- Storage flow design
- Exception flow design

## Phase 3 - Development

- Authentication module
- Client module
- Event module
- Vendor module
- Payment module
- JSON storage layer implementation
- Gson serialization setup

## Phase 4 - Testing

- Unit testing
- Integration testing
- Exception testing
- JSON read/write testing
- File persistence testing
- GUI workflow testing

## Phase 5 - Documentation

- README
- User manual
- Presentation
- Final report

---

# 11. OOP Implementation Summary

## Encapsulation

- Private fields in model classes.
- Public getters and setters.
- Validation through service classes.

## Inheritance

- `User -> Admin, Staff`
- `Event -> WeddingEvent, SeminarEvent, BirthdayEvent`
- `Vendor -> CateringVendor, DecorationVendor, PhotographyVendor`
- `Payment -> CashPayment, TransferPayment, EWalletPayment`

## Abstraction

- `abstract Event`
- `abstract Payment`

## Polymorphism

- `calculateBudget()`
- `processPayment()`
- `generateInvoice()`

---

# 12. Exception Handling

## Custom Exceptions

- `ValidationException`
- `PaymentException`
- `EventException`

## Rules

- Services throw custom exceptions for invalid business conditions.
- Storage classes catch file I/O exceptions.
- GUI classes display friendly error messages.
- File persistence errors must not crash the application without explanation.
