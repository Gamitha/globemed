# 📌 Git Commit Guide – GlobeMed Healthcare Management System

This file defines how to write consistent, meaningful commits and provides a **roadmap** of suggested commits for each assignment part.

---

## 🔖 Commit Message Format

```
<type>(module): <short summary>

[optional longer description with reasoning]
```

### Commit Types

* **feat** → new functionality (pattern implementation, modules)
* **fix** → bug fixes or corrections
* **docs** → documentation, UML diagrams, reports
* **refactor** → internal improvements without changing behavior
* **test** → adding or updating tests
* **chore** → setup, config, or non-functional changes

---

## ✅ Example Commit Messages

* `feat(patient-records): implement Adapter pattern for secure patient data access`
* `feat(scheduling): add Command pattern for appointment booking with conflict resolution`
* `feat(billing): implement Chain of Responsibility for claims approval process`
* `feat(auth): add role-based access control using Proxy pattern`
* `feat(reports): introduce Visitor pattern for flexible report generation`
* `feat(security): add Decorator-based encryption and access logging`
* `docs(diagram): add UML class diagram for appointment scheduling (Command pattern)`
* `test(billing): add unit tests for claim handler chain`
* `refactor(auth): simplify role validation logic`

---

## 🗂️ Commit Roadmap

Follow this sequence while implementing:

### Part A – Patient Records

1. `feat(patient-records): setup Adapter pattern for patient records storage`
2. `feat(patient-records): add secure access controls for medical history retrieval`
3. `test(patient-records): add tests for authorized vs unauthorized access`

### Part B – Appointment Scheduling

4. `feat(scheduling): implement Command pattern for appointment booking`
5. `docs(scheduling): add UML diagram for command-based scheduling`
6. `feat(scheduling): add multi-location conflict resolution logic`

### Part C – Billing & Insurance

7. `feat(billing): setup Chain of Responsibility for billing and claims`
8. `feat(billing): add handlers for validation and insurance approval`
9. `test(billing): add unit tests for claim handler workflow`

### Part D – Roles & Permissions

10. `feat(auth): implement role-based access control using Proxy pattern`
11. `feat(auth): restrict patient data access by role`
12. `refactor(auth): clean up role validation logic`

### Part E – Report Generation

13. `feat(reports): introduce Visitor pattern for report generation`
14. `feat(reports): implement financial and diagnostic report visitors`
15. `docs(reports): document benefits of Visitor pattern`

### Part F – Security Enhancements

16. `feat(security): implement Decorator for encryption and logging`
17. `feat(security): add authentication and authorization middleware`
18. `test(security): add tests for encryption and unauthorized access`

---

✅ With this roadmap, each design pattern is implemented step-by-step and tracked with clear commits.
