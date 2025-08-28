# 🗄️ MapDB Database Implementation Instructions

## 🛠️ Database Framework & Libraries
Use MapDB embedded database for secure, ACID-compliant data persistence with built-in encryption support for healthcare data compliance.

## 📂 Patient Records Database Implementation
"Generate a PatientRepository class using Adapter pattern to convert between domain Patient objects and MapDB serializable format."
"Implement encrypted patient data storage using Decorator pattern to add encryption layer to basic MapDB operations."
"Use Proxy pattern to enforce role-based access control before any MapDB patient record operations."
"Apply Facade pattern to simplify complex patient data operations (create, encrypt, store, audit) into single method calls."
"Create patient record cloning using Prototype pattern for creating template patients with MapDB persistence."
"Use Composite pattern to group related patient data (personal info, medical history, insurance) in MapDB collections."

## 📅 Appointment Scheduling Database
"Create AppointmentRepository using Builder pattern to construct complex appointment objects before MapDB storage."
"Implement appointment conflict detection using Chain of Responsibility pattern through validation handlers before MapDB commit."
"Use State pattern to manage appointment status transitions (requested → confirmed → completed) with MapDB persistence."
"Apply Mediator pattern to coordinate MapDB operations between patient, doctor, and appointment repositories."
"Create appointment queries using Iterator pattern to traverse MapDB BTreeMap collections efficiently."
"Use Bridge pattern to separate appointment scheduling logic from MapDB storage implementation."

## 💳 Billing & Insurance Database
"Implement BillingRepository with Chain of Responsibility pattern for processing claims through validation → insurance → payment handlers with MapDB persistence."
"Use Builder pattern for constructing complex billing objects with multiple components before MapDB storage."
"Apply Bridge pattern to separate billing abstraction from MapDB implementation details for multiple insurance providers."
"Create billing audit trails using Memento pattern to store previous billing states in MapDB collections."
"Use Composite pattern to group billing components (services, medications, procedures) in hierarchical MapDB structure."
"Apply Decorator pattern to add logging and encryption layers to billing MapDB operations."

## 🔐 Database Security & Access Control
"Implement database access using Proxy pattern to wrap all MapDB operations with authentication and authorization checks."
"Use Composite pattern for grouping user permissions (read-only, write, admin) stored in MapDB user management collections."
"Apply Facade pattern to simplify complex security operations (encryption, authentication, logging) into unified MapDB interface."
"Create audit logging using Decorator pattern to wrap MapDB operations with automatic audit trail generation."
"Use Flyweight pattern for efficient session management objects stored in MapDB collections."
"Apply Chain of Responsibility pattern for security validation layers before MapDB data access."

## 📊 Database Report Generation
"Use Visitor pattern to traverse different MapDB data structures (patients, appointments, billing) for report generation."
"Apply Iterator pattern to efficiently process large MapDB collections for bulk reporting operations."
"Create report templates using Prototype pattern for cloning standard report structures with MapDB data population."
"Use Memento pattern to store and restore report generation states in MapDB for resuming interrupted reports."
"Apply Builder pattern for constructing complex reports with multiple data sources from MapDB collections."
"Use Bridge pattern to separate report generation logic from MapDB data access implementation."

## 🏗️ Database Architecture & Configuration
"Generate MapDB configuration using Builder pattern for database setup with encryption, indexing, and performance settings."
"Apply Facade pattern to create simple interface for complex MapDB initialization, connection management, and cleanup operations."
"Use Adapter pattern to integrate MapDB with existing data access interfaces in the healthcare system."
"Create database connection management using Flyweight pattern for efficient MapDB instance reuse."
"Apply Prototype pattern for creating configured MapDB instances based on template configurations."
"Use Bridge pattern to separate database abstraction from MapDB-specific implementation details."

## 🔄 Database Operations & Transactions
"Implement transaction management using Decorator pattern to wrap MapDB operations with automatic transaction handling."
"Use Chain of Responsibility pattern for data validation pipeline before MapDB persistence operations."
"Apply Composite pattern to group related database operations into single transactional units in MapDB."
"Create bulk operations using Iterator pattern to efficiently process large datasets in MapDB collections."
"Use Memento pattern to implement database rollback functionality by storing operation states in MapDB."
"Apply Mediator pattern to coordinate complex multi-table MapDB operations across different repositories."

## 🛡️ Database Backup & Recovery
"Implement backup operations using Visitor pattern to traverse all MapDB collections and export data systematically."
"Use Chain of Responsibility pattern for backup validation pipeline (integrity check → encryption → storage)."
"Apply Memento pattern to create point-in-time database snapshots using MapDB export functionality."
"Create database migration using Adapter pattern to convert between different MapDB schema versions."
"Use Builder pattern for constructing backup configurations with encryption and compression settings."
"Apply Facade pattern to simplify complex backup and recovery operations into simple method calls."

## 🎯 Database Integration with Existing System
"Integrate MapDB with MVC pattern by creating database-backed Models using Composite pattern for data grouping."
"Use Adapter pattern to connect existing Controllers with new MapDB repository implementations."
"Apply Observer pattern through Mediator to notify UI components of MapDB data changes automatically."
"Create JGoodies UI integration using Bridge pattern to separate UI logic from MapDB data access."
"Use Proxy pattern to add caching layer between JGoodies components and MapDB operations."
"Apply Decorator pattern to add validation and formatting layers to MapDB data before UI display."