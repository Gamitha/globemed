# 🚀 Copilot Instructions for GlobeMed Healthcare Management System

This file contains ready-to-use prompts you can paste into GitHub Copilot (or any AI coding assistant) to generate implementation code, UML diagrams, or explanations for each part of the assignment.

It also specifies **Object-Oriented Design Patterns** that should be applied across different modules of the project for scalability, maintainability, and security.

---

## 🧩 Design Patterns to Use

When implementing code, apply the following OOP patterns where suitable:

* **Adapter**
* **Decorator**
* **Chain of Responsibility**
* **Composite**
* **Builder**
* **Bridge**
* **Facade**
* **Flyweight**
* **Proxy**
* **Iterator**
* **Mediator**
* **Memento**
* **State**
* **Visitor**
* **MVC (Composite Pattern)**

---

## 🛠️ Framework & Libraries

* **Use the [JGoodies library](https://www.jgoodies.com/) for UI design and layout management.**
  It simplifies building clean, maintainable Swing UIs that align well with MVC and other design patterns.

---

## 📂 Patient Records (Secure Data Management)

* "Generate a class structure using the Adapter pattern for managing patient records securely with support for authorized access only."
* "Write Java code demonstrating how to retrieve patient medical history securely with access control checks."
* "Explain in comments how the Adapter pattern ensures interoperability and security."
* "Use Proxy pattern to enforce role-based access control."
* "Apply Decorator for adding logging and encryption layers."

---

## 📅 Appointment Scheduling

* "Provide a UML class diagram for an appointment scheduling system using the Command pattern."
* "Write code that encapsulates appointment booking requests as commands and executes them with conflict checks."
* "Demonstrate handling multi-location scheduling conflicts in code."
* "Use Mediator pattern to coordinate between doctors, nurses, and patients."
* "Apply State pattern for appointment status transitions (requested → confirmed → completed)."

---

## 💳 Billing & Insurance Claims

* "Generate code that implements the Chain of Responsibility pattern for billing and multi-step insurance claim approval."
* "Write an example where claims pass through billing → validation → insurance approval → settlement handlers."
* "Comment the code to explain how scalability and flexibility are achieved."
* "Use Builder pattern for constructing complex billing objects."
* "Apply Bridge pattern to separate abstraction (billing) from implementation (insurance providers)."

---

## 🔐 Roles & Permissions

* "Write code implementing role-based access control using the Proxy pattern for different user roles (doctor, nurse, pharmacist)."
* "Demonstrate restricting access to patient records based on user role."
* "Include a small example with role assignment and permission checks."
* "Use Composite pattern for grouping permissions (read-only, write, admin)."

---

## 📊 Report Generation

* "Use the Visitor pattern to separate report generation from core patient data objects."
* "Write code to generate both financial and diagnostic reports using the same patient object model."
* "Comment on how the Visitor pattern improves maintainability and flexibility."
* "Apply Iterator pattern to traverse patient records for bulk reporting."
* "Use Memento pattern to store report history and restore previous report states."

---

## 🛡️ Security (Cross-cutting Concerns)

* "Implement the Decorator pattern to add encryption and logging to patient record access."
* "Write code that demonstrates authentication + authorization checks before accessing data."
* "Add comments explaining how this approach mitigates risks like unauthorized access and tampering."
* "Use Facade pattern to simplify complex security subsystems (encryption, authentication, logging)."
* "Apply Flyweight pattern for efficient handling of repeated security objects (like sessions)."

---

## 🎯 System-Wide Architecture

* "Apply MVC (Composite) pattern for structuring the application into Models, Views, and Controllers."
* "Use Mediator for managing communication between subsystems (e.g., appointment, billing, records)."
* "Leverage State and Memento patterns for maintaining application states and rollback mechanisms."
* "Integrate JGoodies library into the MVC-based UI layer for cleaner layout and separation of concerns."

---

✅ With these prompts and patterns, Copilot can generate code and designs aligned with enterprise-level software engineering practices, while JGoodies ensures clean UI implementation.
