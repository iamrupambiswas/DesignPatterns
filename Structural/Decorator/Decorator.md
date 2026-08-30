# Decorator Pattern

## Problem
You need to add **extra behavior** to an object dynamically — e.g. a `Coffee` that can optionally have `Milk`, `Sugar`, `Whip` added, each affecting price and description — without hardcoding every possible combination, and without modifying the original class.

## Naive Solution
```java
public class Coffee { ... }
public class CoffeeWithMilk extends Coffee { ... }
public class CoffeeWithSugar extends Coffee { ... }
public class CoffeeWithMilkAndSugar extends Coffee { ... }
public class CoffeeWithMilkAndSugarAndWhip extends Coffee { ... }
```
A subclass for every possible combination of extras.

## Why It Becomes Problematic
- **Combinatorial explosion**: 3 optional extras → up to 8 possible combinations → up to 8 subclasses. Add a 4th extra and it doubles again.
- Combinations are fixed at compile time — you can't add "extra shot of espresso" to an existing cup **at runtime** based on user choice.
- Violates the Open/Closed Principle: every new extra means creating new subclasses for every existing combination it should work with.
- Logic for each extra (price, description) gets duplicated across all the combination subclasses that include it.

## Pattern
**Decorator**: attach additional responsibilities to an object dynamically by **wrapping** it in decorator objects that share the same interface as the original — behavior is added by composition, layer by layer, instead of by subclassing every combination.

Key ideas:
- Both the base object and the decorators implement the same common interface.
- Each decorator **wraps** another object of that same interface and delegates to it, adding its own behavior before/after.
- Decorators can be stacked in any combination, at runtime, in any order.

## Java Implementation
```java
// Common interface
public interface Coffee {
    double getCost();
    String getDescription();
}

// Base component
public class SimpleCoffee implements Coffee {
    public double getCost() { return 2.0; }
    public String getDescription() { return "Coffee"; }
}
```

```java
// Abstract decorator — wraps a Coffee, implements the same interface
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee wrapped;

    protected CoffeeDecorator(Coffee wrapped) {
        this.wrapped = wrapped;
    }
}

// Concrete decorators
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee wrapped) { super(wrapped); }

    public double getCost() { return wrapped.getCost() + 0.5; }
    public String getDescription() { return wrapped.getDescription() + " + Milk"; }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee wrapped) { super(wrapped); }

    public double getCost() { return wrapped.getCost() + 0.2; }
    public String getDescription() { return wrapped.getDescription() + " + Sugar"; }
}

public class WhipDecorator extends CoffeeDecorator {
    public WhipDecorator(Coffee wrapped) { super(wrapped); }

    public double getCost() { return wrapped.getCost() + 0.7; }
    public String getDescription() { return wrapped.getDescription() + " + Whip"; }
}
```

Usage:
```java
Coffee order = new WhipDecorator(new SugarDecorator(new MilkDecorator(new SimpleCoffee())));

System.out.println(order.getDescription()); // Coffee + Milk + Sugar + Whip
System.out.println(order.getCost());        // 2.0 + 0.5 + 0.2 + 0.7 = 3.4
```
Any combination, in any order, chosen at runtime — no new subclass needed for each variant.

## Real Spring Example
Java's own I/O classes are the classic Decorator example (`new BufferedReader(new InputStreamReader(new FileInputStream(file)))`), and Spring builds on the same idea in its web layer with **request/response wrappers**:

```java
public class LoggingRequestWrapper extends HttpServletRequestWrapper {

    public LoggingRequestWrapper(HttpServletRequest request) {
        super(request); // wraps the original request, same interface
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        System.out.println("Accessed param: " + name + "=" + value);
        return value;
    }
}
```
```java
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {
        HttpServletRequest wrapped = new LoggingRequestWrapper(request); // decorate
        chain.doFilter(wrapped, response); // rest of the chain uses it transparently
    }
}
```
`HttpServletRequestWrapper` implements the same `HttpServletRequest` interface it wraps — downstream filters and controllers use `wrapped` exactly like a normal request, unaware it's been decorated with logging. Spring's `TransactionAwareDataSourceProxy` and `@Cacheable` AOP proxies follow the same underlying idea: wrap the original, add behavior, keep the same interface.

## When NOT to Use It
- When you only need **one fixed combination** of extra behavior, always — a single subclass or straightforward composition is simpler than a decorator chain.
- When decorators must be applied in a **specific, order-dependent way** and getting the order wrong silently produces incorrect behavior — the flexibility becomes a footgun; consider Builder or explicit configuration instead.
- When debugging deeply nested decorator chains — stack traces and object graphs get harder to read the more layers you wrap (`new A(new B(new C(new D(...))))`), which can hurt maintainability.
- When the "extra behavior" needs access to internals of the wrapped object beyond its public interface — Decorator only works cleanly when everything happens through the shared interface.