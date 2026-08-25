# Abstract Factory Pattern

## Problem
You need to create **families of related objects** that must be used together — e.g. UI components for different themes (`Light` vs `Dark`), or database drivers for different vendors (`MySQL` vs `PostgreSQL`: connection + query builder + transaction manager). Mixing pieces from different families (a `LightButton` with a `DarkCheckbox`) would be a bug.

## Naive Solution
```java
public class UIFactory {

    public Button createButton(String theme) {
        if (theme.equals("DARK")) return new DarkButton();
        return new LightButton();
    }

    public Checkbox createCheckbox(String theme) {
        if (theme.equals("DARK")) return new DarkCheckbox();
        return new LightCheckbox();
    }
}
```
Every caller passes `theme` around and calls both methods separately, hoping they stay consistent.

## Why It Becomes Problematic
- **Nothing enforces consistency** — a caller could accidentally request a `LightButton` and a `DarkCheckbox`, since each method is chosen independently.
- The `theme` string/flag has to be threaded through every method call — easy to forget, easy to typo.
- Adding a new family member (e.g. `Slider`) means editing this class again and again (Open/Closed Principle violation), same problem as the plain Factory.
- As families grow (5-10 related components), the single factory class becomes a tangle of if/else per method.

## Pattern
**Abstract Factory**: provide an interface for creating **families of related objects** without specifying their concrete classes — one factory object per family, guaranteeing everything it produces belongs together.

Key ideas:
- Define an abstract factory interface with one creation method per product type.
- Each concrete factory (one per family) implements all methods, returning matching concrete products.
- Client code depends only on the abstract factory + abstract products — picks *one* concrete factory, then gets a consistent family "for free."

## Java Implementation
```java
// Abstract products
public interface Button {
    void render();
}
public interface Checkbox {
    void render();
}

// Light family
public class LightButton implements Button {
    public void render() { System.out.println("Rendering light button"); }
}
public class LightCheckbox implements Checkbox {
    public void render() { System.out.println("Rendering light checkbox"); }
}

// Dark family
public class DarkButton implements Button {
    public void render() { System.out.println("Rendering dark button"); }
}
public class DarkCheckbox implements Checkbox {
    public void render() { System.out.println("Rendering dark checkbox"); }
}
```

```java
// Abstract factory
public interface UIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// Concrete factories — each guarantees a matching family
public class LightUIFactory implements UIFactory {
    public Button createButton() { return new LightButton(); }
    public Checkbox createCheckbox() { return new LightCheckbox(); }
}

public class DarkUIFactory implements UIFactory {
    public Button createButton() { return new DarkButton(); }
    public Checkbox createCheckbox() { return new DarkCheckbox(); }
}
```

Usage:
```java
UIFactory factory = darkModeEnabled ? new DarkUIFactory() : new LightUIFactory();

Button button = factory.createButton();
Checkbox checkbox = factory.createCheckbox();
button.render();
checkbox.render();
// Impossible to mix a DarkButton with a LightCheckbox — factory guarantees the family.
```

## Real Spring Example
Spring's `JdbcTemplate`/`DataSource` ecosystem doesn't hand you a textbook Abstract Factory class, but the pattern shows up conceptually with **vendor-specific factory beans** — one factory per "family" of database-related objects:

```java
public interface DatabaseFactory {
    DataSource createDataSource();
    SqlDialect createDialect();
}

@Component
@ConditionalOnProperty(name = "db.vendor", havingValue = "postgres")
public class PostgresDatabaseFactory implements DatabaseFactory {
    public DataSource createDataSource() { return new PostgresDataSource(); }
    public SqlDialect createDialect() { return new PostgresDialect(); }
}

@Component
@ConditionalOnProperty(name = "db.vendor", havingValue = "mysql")
public class MySqlDatabaseFactory implements DatabaseFactory {
    public DataSource createDataSource() { return new MySqlDataSource(); }
    public SqlDialect createDialect() { return new MySqlDialect(); }
}
```
```java
@Service
public class QueryService {

    private final DatabaseFactory databaseFactory; // Spring injects the matching @ConditionalOnProperty bean

    public QueryService(DatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public void run() {
        DataSource ds = databaseFactory.createDataSource();
        SqlDialect dialect = databaseFactory.createDialect();
        // ds and dialect are guaranteed to be from the SAME vendor
    }
}
```
Spring picks the right `DatabaseFactory` bean based on config (`db.vendor=postgres`), and everything downstream gets a consistent, matching family of objects — exactly the guarantee Abstract Factory is meant to provide.

## When NOT to Use It
- When you only have **one product type** to create, not a family — plain Factory is simpler and sufficient.
- When the "families" rarely grow or vary — the extra layer of abstraction (factory interface + N concrete factories) is unnecessary ceremony.
- When adding a **new product type** to the family is more common than adding a new family — Abstract Factory makes this painful, since every concrete factory must implement the new method (violates Open/Closed in the other direction).
- In small apps or scripts where DI/config-driven bean selection (like Spring profiles) already solves the "pick the right consistent implementation" problem without a hand-written factory hierarchy.