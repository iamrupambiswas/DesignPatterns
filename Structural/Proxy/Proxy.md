# Proxy Pattern

## Problem
You need to control access to an object — e.g. delay creating an **expensive object** until it's actually needed, add **access control** before reaching it, or add **caching/logging** around calls to it — without changing the object's own code or the client code that uses it.

## Naive Solution
```java
public class HighResImage {
    private final String filename;

    public HighResImage(String filename) {
        this.filename = filename;
        loadFromDisk(); // expensive I/O, happens immediately even if never displayed
    }

    private void loadFromDisk() {
        System.out.println("Loading " + filename + " from disk...");
    }

    public void display() {
        System.out.println("Displaying " + filename);
    }
}
```
```java
List<HighResImage> gallery = new ArrayList<>();
for (String file : filenames) {
    gallery.add(new HighResImage(file)); // ALL images loaded immediately, even unseen ones
}
```

## Why It Becomes Problematic
- Every `HighResImage` is loaded **eagerly**, even ones the user never scrolls to see — wasted memory and startup time.
- If you want to add **access control** ("only premium users can view this image") or **logging** ("track every view"), you have to modify `HighResImage` itself, mixing unrelated concerns (loading, permissions, logging) into one class.
- Any change to those cross-cutting concerns means editing the core class directly, risking breaking its main responsibility.
- Client code has no way to intercept or control access to the real object without the real object's cooperation.

## Pattern
**Proxy**: provide a **surrogate/placeholder object** that implements the same interface as the real object, controlling access to it — the proxy can defer creation, check permissions, add logging/caching, all while the client code interacts with it exactly as if it were the real thing.

Key ideas:
- Proxy and real object share a common interface.
- Client code depends only on the interface — it doesn't know (or care) whether it's talking to the real object or a proxy.
- The proxy adds its own logic (lazy-loading, access checks, caching, logging) *around* delegating to the real object.

## Java Implementation
```java
// Common interface
public interface Image {
    void display();
}

// Real object — expensive to create
public class HighResImage implements Image {
    private final String filename;

    public HighResImage(String filename) {
        this.filename = filename;
        loadFromDisk(); // expensive, only happens when this class is actually instantiated
    }

    private void loadFromDisk() {
        System.out.println("Loading " + filename + " from disk...");
    }

    @Override
    public void display() {
        System.out.println("Displaying " + filename);
    }
}
```

```java
// Proxy — same interface, controls access + lazy-loads the real object
public class ImageProxy implements Image {
    private final String filename;
    private HighResImage realImage; // not created until needed

    public ImageProxy(String filename) {
        this.filename = filename;
    }

    @Override
    public void display() {
        if (realImage == null) {
            realImage = new HighResImage(filename); // created on first use only
        }
        realImage.display();
    }
}
```

Usage:
```java
List<Image> gallery = new ArrayList<>();
for (String file : filenames) {
    gallery.add(new ImageProxy(file)); // cheap — no loading happens yet
}

gallery.get(2).display(); // ONLY this image gets loaded from disk, right now
```
Client code (`gallery.get(2).display()`) is identical whether working with the real object or the proxy — the interface hides the difference entirely.

## Real Spring Example
Proxy is arguably the **most heavily used pattern in Spring internally** — nearly all of Spring's core magic (`@Transactional`, `@Cacheable`, `@Async`, AOP in general) works by wrapping your bean in a **dynamic proxy**:

```java
@Service
public class OrderService {

    @Transactional // Spring wraps this bean in a proxy at startup
    public void placeOrder(Order order) {
        // your actual business logic
    }
}
```
What actually happens: Spring doesn't inject your raw `OrderService` — it injects a **proxy** implementing the same interface (or extending the class via CGLIB), so that when you call `placeOrder(...)`, the proxy intercepts the call, starts a transaction, delegates to the real method, then commits/rolls back — all invisible to the caller:

```java
// Conceptually, Spring generates something like:
public class OrderServiceProxy extends OrderService {
    private final OrderService target;
    private final PlatformTransactionManager txManager;

    @Override
    public void placeOrder(Order order) {
        TransactionStatus tx = txManager.getTransaction(...);
        try {
            target.placeOrder(order); // delegate to the real object
            txManager.commit(tx);
        } catch (Exception e) {
            txManager.rollback(tx);
            throw e;
        }
    }
}
```
`@Cacheable`, `@Async`, `@Secured` — and Spring Security's method-level authorization — all work the same way: a proxy sits between the caller and the real bean, adding behavior transparently around the call.

## When NOT to Use It
- When there's **no real need to control access** — if the object is cheap to create and there's nothing to intercept (no lazy-loading, security, caching, or logging concern), a direct reference is simpler.
- When proxying adds a layer that makes **debugging harder** — stack traces through dynamic proxies (like Spring's AOP proxies) can be confusing, and self-invocation within the same class silently bypasses the proxy (a classic Spring `@Transactional` gotcha).
- When the added indirection introduces a **noticeable performance cost** in a hot path — every call goes through extra logic; profile if this matters.
- When you need **direct, unmediated access** to the real object's full behavior/state — some proxy types (Java's `Proxy` class, CGLIB subclass proxies) impose constraints (e.g. can't proxy `final` methods/classes) that may conflict with what you need.