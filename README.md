# P2P Betting Exchange — Pure OOP (Java SE 17 / Swing)

![Java](https://img.shields.io/badge/Java-17-orange?logo=java) ![License](https://img.shields.io/github/license/ermi9/p2p-oop-version) ![Stars](https://img.shields.io/github/stars/ermi9/p2p-oop-version?style=social) ![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)

A peer-to-peer sports betting exchange built entirely in pure Java SE 17 with a Swing desktop UI.
No frameworks, no databases, no external APIs — all state lives in in-memory maps inside `Exchange.java`.

---

## How to build and run

```
mvn package -DskipTests
java -jar target/exchange-1.0.0.jar
```

## Demo accounts

Two standard user accounts and one admin account are seeded on startup. You can also register your own account from the login screen.

---

## Project structure

```
src/main/java/com/ermiyas/exchange/
├── Exchange.java                    # Application coordinator (holds all in-memory maps)
├── domain/
│   ├── exception/                   # ExchangeException hierarchy (5 classes)
│   ├── model/
│   │   ├── Money.java               # Final value object — ad-hoc polymorphism
│   │   ├── Odds.java                # Final value object — calculateLiability()
│   │   ├── Password.java            # Final value object — SHA-256, hash never exposed
│   │   ├── CommissionPolicy.java    # Plain class — rate + apply(Money)
│   │   ├── Wallet.java              # No back-reference to User
│   │   ├── Offer.java               # Partial fills — List<Bet>
│   │   ├── Bet.java                 # resolve(Outcome, Wallet, Wallet, CommissionPolicy)
│   │   ├── Event.java               # Owns List<Offer>, processResult(SettlementStrategy)
│   │   └── user/
│   │       ├── User.java            # Abstract — authenticate(), getRoleName()
│   │       ├── StandardUser.java    # extends User implements Tradeable
│   │       ├── AdminUser.java       # extends User, no wallet
│   │       └── Tradeable.java       # Interface — deposit/withdraw hook (ISP)
│   └── settlement/
│       ├── SettlementStrategy.java       # Interface — OCP / DIP
│       ├── ThreeWaySettlementStrategy    # HOME_WIN / AWAY_WIN / DRAW
│       └── HeadToHeadSettlementStrategy  # HOME_WIN / AWAY_WIN only
└── ui/
    ├── ExchangeApp.java   # main() — seeds events and demo accounts
    ├── LoginWindow.java   # CardLayout: login / register
    ├── MainWindow.java    # JTabbedPane — Markets, Activity, Wallet, Admin
    ├── MarketsPanel.java
    ├── ActivityPanel.java
    ├── WalletPanel.java
    └── AdminPanel.java
```

---

## OOP concepts demonstrated

| Concept | Where |
|---|---|
| Information hiding | Private fields, final value objects |
| Encapsulation | Money, Wallet, Offer, Bet |
| Inheritance | User → StandardUser / AdminUser; exception hierarchy |
| Abstraction | SettlementStrategy, Tradeable |
| Composition | Event→List\<Offer\>, Offer→List\<Bet\>, StandardUser→Wallet |
| Ad-hoc polymorphism | Money.plus(Money) vs Money.plus(BigDecimal) vs Money.multiply(BigDecimal) |
| Inclusion polymorphism | User references — login returns User, getRoleName() dispatches at runtime |
| Coercion polymorphism | Money.of(String) → Money.of(BigDecimal) |
| Parametric polymorphism | Exchange's Map\<Long,User\>, List\<Offer\> in Event, etc. |
| Multityping | StandardUser as StandardUser + User + Tradeable simultaneously |
| OCP | SettlementStrategy — new market type without touching Event |
| LSP | AdminUser has no getWallet() — no exception-throwing override |
| ISP | Tradeable is focused (wallet ops only); SettlementStrategy is focused |
| DIP | Exchange.deposit/withdraw depend on Tradeable; Event.processResult depends on SettlementStrategy |

---

## Market types

**THREE_WAY** — full 1X2 market. Maker backs one outcome; taker's counter-position covers the remaining two (e.g., back Home Win → taker wins on Away Win or Draw).

**HEAD_TO_HEAD** — two-outcome market only. Draw is not a valid result; if the match ends in a draw the strategy throws. Eliminates the double-chance counter-position entirely.
