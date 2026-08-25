# Prototype Pattern

## Problem
You need to create a new object that's **almost identical** to an existing one (a document template, a game character, a config object with heavy setup), and creating it from scratch is expensive or repetitive.

## Naive Solution
```java
Report original = new Report();
original.setTitle("Q3 Sales");
original.setAuthor("Alex");
original.loadTemplateFromDisk(); // expensive I/O
original.setSections(buildDefaultSections()); // expensive computation

// Need a similar report for Q4...
Report copy = new Report();
copy.setTitle("Q4 Sales");
copy.setAuthor("Alex");
copy.loadTemplateFromDisk();       // redo the expensive work
copy.setSections(buildDefaultSections()); // redo it again
```

## Why It Becomes Problematic
- **Re-running expensive setup** (I/O, computation, network calls) every time you need a "mostly the same" object wastes time and resources.
- Constructor logic may depend on the object's **internal/private state** that the caller doesn't have access to (or shouldn't need to know about) — so recreating it externally isn't always possible.
- Duplicated setup code spreads across the app wherever a similar object is needed — violates DRY.
- If the class has many fields, manually copying each one field-by-field via setters is tedious and error-prone (easy to forget one).

## Pattern
**Prototype**: create new objects by **cloning an existing instance** (the "prototype") instead of instantiating from scratch, then only tweak the fields that differ.

Key ideas:
- The class implements a `clone()` method (or a `copy` constructor) that produces a duplicate of itself.
- Cloning bypasses expensive re-initialization — the new object starts as a copy of already-configured state.
- Caller clones the prototype, then modifies just what's different.

## Java Implementation
```java
public class Report implements Cloneable {
    private String title;
    private String author;
    private List<String> sections;

    public Report(String title, String author) {
        this.title = title;
        this.author = author;
        this.sections = loadDefaultSections(); // expensive setup, done once
    }

    private List<String> loadDefaultSections() {
        // simulate expensive work: I/O, computation, etc.
        return new ArrayList<>(List.of("Summary", "Charts", "Appendix"));
    }

    @Override
    public Report clone() {
        try {
            Report copy = (Report) super.clone();
            copy.sections = new ArrayList<>(this.sections); // deep-copy mutable fields
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // won't happen, we implement Cloneable
        }
    }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }
}
```

Usage:
```java
Report q3Report = new Report("Q3 Sales", "Alex"); // expensive setup happens once

Report q4Report = q3Report.clone(); // cheap copy, no re-setup
q4Report.setTitle("Q4 Sales");      // only change what's different
```

> **Note on `Cloneable`:** Java's built-in `Cloneable`/`clone()` is notoriously clunky (shallow copy by default, checked exception, no interface contract on `clone()` itself). Many real codebases prefer a **copy constructor** instead:
```java
public Report(Report other) {
    this.title = other.title;
    this.author = other.author;
    this.sections = new ArrayList<>(other.sections);
}
// usage: Report q4Report = new Report(q3Report);
```
Both express the same Prototype idea — the copy constructor is just a more idiomatic Java implementation.

## Real Spring Example
Spring exposes Prototype directly as a **bean scope**, distinct from the default singleton scope:

```java
@Component
@Scope("prototype")
public class ShoppingCart {
    private final List<String> items = new ArrayList<>();

    public void addItem(String item) {
        items.add(item);
    }
}
```
```java
@Service
public class CheckoutService {

    private final ApplicationContext context;

    public CheckoutService(ApplicationContext context) {
        this.context = context;
    }

    public ShoppingCart newCart() {
        // Spring returns a FRESH instance every time, not a shared singleton
        return context.getBean(ShoppingCart.class);
    }
}
```
Each call to `getBean(ShoppingCart.class)` produces a brand-new instance — exactly the "give me a new object based on a template configuration" idea Prototype embodies, just managed by the container instead of a manual `clone()` call. (Spring doesn't literally clone an existing instance here — it re-runs bean creation — but the *scope* concept, "one fresh copy per request," is the same problem Prototype solves.)

## When NOT to Use It
- When object creation is **cheap** — cloning adds complexity for no real performance benefit.
- When the object holds resources that **shouldn't be duplicated** blindly — e.g. an open database connection or file handle; cloning those naively causes bugs (two objects pointing at the same resource, or both trying to close it).
- When deep-copy logic becomes complicated (nested objects, circular references) — the `clone()`/copy-constructor maintenance burden can outweigh the benefit; a Builder or plain constructor may be clearer.
- When mutable shared state inside the prototype is easy to forget to deep-copy — a shallow copy bug (two "independent" objects secretly sharing a `List` or `Map`) is a classic, hard-to-spot source of errors.