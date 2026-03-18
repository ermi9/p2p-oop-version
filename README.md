# 🧩 FairPlay: P2P Sports Betting Exchange (Student Project)

## 🌟 Overview
This repository contains a peer-to-peer (P2P) sports betting exchange built as a learning-focused student project. The primary goal is not to ship a commercial product, but to deeply understand and implement the mechanics of an exchange-style system—where users bet against each other, not the platform.

**Key Characteristics:**
* **Users define their own odds:** No house-set prices.
* **Platform takes no risk:** The system acts strictly as an escrow and matching engine.
* **Reservation-based accounting:** Funds are explicitly reserved to prevent double-spending.
* **Deterministic Settlement:** Markets are settled based on verified external data.

---

## 🎯 Project Philosophy: Learning Over Frameworks
This project is intentionally designed to explore the "Why" before the "How":
* **Core Logic First:** We start with domain logic (What is an offer? How is risk calculated?) before worrying about the UI shell.
* **Clean Architecture:** Business rules are isolated from Spring Boot and databases, allowing the infrastructure to remain replaceable.
* **Transparent Mechanics:** Every step—from posting an offer to matching and final settlement—is explicit and traceable.



---

## 🛠️ Current Features & Architecture

### 1. The Strategy Pattern (Market Intelligence)
The backend uses a polymorphic **Strategy Pattern** to handle different market types. A notable feature is the **Outlier Detection Logic**:
* **Consensus Filtering:** The system calculates a median price from multiple bookmaker sources.
* **Data Sanitization:** It automatically discards "junk" odds (e.g., 980.00) that often appear in external APIs during market suspensions.

### 2. Modular Frontend
The UI has been refactored for professional scalability:
* **UI Components:** Logic is split between data controllers (e.g., `trading.js`) and reusable UI helpers (e.g., `trading-ui.js`).
* **Unified Styling:** A global layout engine (`base.css`) and component library (`components.css`) ensure a consistent user experience.

### 3. Secure Accounting
Wallets use a reservation system. When you place a bet, your money isn't "gone"—it is **Reserved (Escrowed)** until the match is over, ensuring the winner can always be paid.

---

## 📂 High-Level Structure

```text
.
├── frontend/                   # Web interface (HTML, CSS, JS, Assets)
│   ├── css/                    # Modular stylesheets (base, components)
│   ├── js/                     # Feature logic and UI component helpers
│   └── logos/                  # Team and league branding
├── src/main/java/com/ermiyas/exchange/
│   ├── api/                    # REST Controllers and DTOs
│   ├── application/            # Business services and orchestration
│   ├── Config/                 # Security and system configuration
│   ├── domain/                 # Core logic, Entities, Repositories, and VOs
│   └── infrastructure/         # External API clients and JPA persistence
├── src/main/resources/         # Configuration properties
├── pom.xml                     # Maven build configuration
└── README.md                   # Project documentation
```
## 🚧 Current Status: "Not Perfect Yet"
This project is an evolving learning vehicle and is not yet production-ready.

### Known Areas for Improvement:
- **Authentication**: Currently using LocalStorage; needs a transition to JWT or Secure Sessions.
- **Real-time Updates**: Looking to implement WebSockets for instant, real-time offer matching.
- **Test Coverage**: While core domain entities are tested, integration testing for the external API strategies is ongoing.

## 🤝 Collaborations & Feedback
I am very open to feedback, suggestions, and collaborations! Whether you are a student, a developer, or a reviewer, I’d love to hear your thoughts on:

- **Domain Modeling**: Are there better ways to handle asymmetric risk?
- **Architecture**: How can the separation between layers be even cleaner?
- **Edge Cases**: What sports-specific scenarios am I missing?

### How to Contribute:
- Feel free to open Issues for bugs or feature ideas.
- Pull Requests are welcome—please ensure your code follows the existing modular structure.
- Reach out if you want to discuss the project mechanics in detail!

## 📌 Final Note
Building this from the inside out has been a journey of discovering complexity rather than hiding it. Thank you for taking the time to explore FairPlay.
