# Singleton Pattern

## Problem
You need exactly **one** instance of a class across the whole application — e.g. a config manager, a connection pool, a logger — and every part of the code should talk to the *same* instance.

## Naive Solution
```java
public class ConfigManager {
    public ConfigManager() {
        // loads config from file
    }
}
```
Anyone can do `new ConfigManager()` anywhere, anytime.

## Why It Becomes Problematic
- Multiple instances → inconsistent state (each one might load config differently, or waste memory/resources reopening connections).
- No central control over creation — you can't guarantee "only one" exists.
- In multithreaded code, two threads could each create their own instance, defeating the purpose entirely.

## Pattern
**Singleton**: restrict instantiation of a class to one single instance, and provide a global access point to it.

Key ideas:
- Make the constructor `private`.
- Store the single instance in a `private static` field.
- Expose it via a `public static getInstance()` method.

## Java Implementation

### Simple (not thread-safe)
```java
public class ConfigManager {
    private static ConfigManager instance;

    private ConfigManager() {
        // private constructor blocks external "new"
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
}
```

### Thread-safe & lazy (recommended: initialization-on-demand holder)
```java
public class ConfigManager {

    private ConfigManager() {
    }

    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }
}
```
The JVM guarantees the `Holder` class (and thus `INSTANCE`) is initialized only once, lazily, and thread-safely — no `synchronized` keyword needed.

### Enum Singleton (simplest, serialization-safe)
```java
public enum ConfigManager {
    INSTANCE;

    public void loadConfig() {
        // ...
    }
}
// usage: ConfigManager.INSTANCE.loadConfig();
```

## Real Spring Example
You rarely write a manual singleton in Spring — the **container does it for you**.

```java
@Service
public class PaymentService {
    // Spring creates ONE instance of this bean by default
    // and injects the same instance everywhere it's needed.
}
```
```java
@Component
public class OrderController {

    private final PaymentService paymentService;

    public OrderController(PaymentService paymentService) {
        this.paymentService = paymentService; // same shared instance
    }
}
```
By default, every `@Component`, `@Service`, `@Repository` bean has **singleton scope** (`@Scope("singleton")` is implicit). Spring manages the lifecycle, so you don't need private constructors or `getInstance()` — dependency injection replaces the classic pattern.

## When NOT to Use It
- When the "shared instance" holds **mutable state** and is used across threads without synchronization — you get hard-to-debug race conditions.
- When it's used as a disguised **global variable** to avoid passing dependencies properly — this hurts testability (hard to mock) and hides dependencies.
- When you need **different configurations per context** (e.g. multi-tenant apps) — a true singleton can't vary per request/tenant.
- In most Spring apps — let the container manage the scope instead of hand-rolling `getInstance()`.