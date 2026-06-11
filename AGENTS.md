# EVO Project Rules

## Stack

- Java 17
- Java Swing
- JSON File Storage
- Gson for JSON serialization
- Gson `.jar` in `lib/`

## Project Structure

```text
EVO/
|-- .vscode/
|   `-- settings.json
|-- data/
|   |-- users.json
|   |-- clients.json
|   |-- events.json
|   |-- vendors.json
|   `-- payments.json
|-- docs/
|   `-- PRD.md
|-- lib/
|   `-- gson-2.10.1.jar
|-- src/
|   |-- model/
|   |-- service/
|   |-- storage/
|   |-- gui/
|   |-- util/
|   |-- exception/
|   `-- main/
`-- README.md
```

## Architecture Rules

- Use the flow `GUI -> Service -> Storage`.
- GUI classes handle screens, forms, tables, and user interaction only.
- Service classes handle validation, business rules, calculations, and orchestration.
- Storage classes handle JSON file I/O only.
- GUI must never directly access JSON files.
- GUI must never contain persistence logic.

## Storage Rules

- Never use relational query language persistence.
- Never use Java database connectivity APIs.
- Never use connection utility classes for external data stores.
- Do not add database connection utilities.
- Do not add query strings to the project.
- Use local JSON files in the `data/` directory for all persistent data.

## Persistence Rules

- All persistent data must be stored in JSON files.
- Use Gson from the `.jar` file in `lib/` for serialization and deserialization.
- Each module must load data from JSON on startup.
- Each module must save data to JSON after create, update, or delete operations.
- Storage classes should use try-catch for file operations.
- Storage classes should not contain business logic.

## Storage Package

Required storage classes:

- `JsonStorage.java`
- `UserStorage.java`
- `ClientStorage.java`
- `EventStorage.java`
- `VendorStorage.java`
- `PaymentStorage.java`

## Coding Rules

- Follow OOP principles.
- Use private fields.
- Use getters and setters.
- Keep model classes focused on data and behavior.
- Keep service classes focused on business logic.
- Keep storage classes focused on file persistence.
- Keep GUI classes focused on presentation and user interaction.

## Naming Convention

Class:
PascalCase

Method:
camelCase

JSON fields:
camelCase

File names:
snake_case or plural module names, for example `users.json`

## OOP Requirements

Must demonstrate:

- Encapsulation
- Inheritance
- Abstraction
- Polymorphism

## Required Inheritance

- `User -> Admin, Staff`
- `Event -> WeddingEvent, SeminarEvent, BirthdayEvent`
- `Vendor -> CateringVendor, DecorationVendor, PhotographyVendor`
- `Payment -> CashPayment, TransferPayment, EWalletPayment`

## Required Abstraction

- `abstract Event`
- `abstract Payment`

## Required Polymorphism

- `calculateBudget()`
- `processPayment()`
- `generateInvoice()`

## Error Handling

Custom exceptions:

- `ValidationException`
- `PaymentException`
- `EventException`

All file operations must use try-catch.
