# Flyweight Pattern

## Problem
You need to create a **huge number of similar objects** — e.g. millions of characters in a text editor, or thousands of trees in a forest simulation — and creating a full object for each one wastes massive amounts of memory, most of which is duplicated data.

## Naive Solution
```java
public class Tree {
    private String type;        // "Oak", "Pine", ...
    private byte[] textureData; // heavy, shared visual data (e.g. several MB)
    private String color;
    private int x, y;           // position, unique per tree

    public Tree(String type, byte[] textureData, String color, int x, int y) {
        this.type = type;
        this.textureData = textureData;
        this.color = color;
        this.x = x;
        this.y = y;
    }
}
```
```java
List<Tree> forest = new ArrayList<>();
for (int i = 0; i < 1_000_000; i++) {
    forest.add(new Tree("Oak", loadTexture("oak.png"), "Green", randomX(), randomY()));
}
```

## Why It Becomes Problematic
- Each `Tree` **duplicates** the same heavy `textureData` (potentially megabytes) — with a million trees, you're storing that texture a million times over.
- Memory usage explodes even though most of the data (type, texture, color) is **identical** across many objects — only position genuinely varies per tree.
- Loading/copying the same texture repeatedly also wastes CPU and I/O, not just memory.
- The application may run out of memory or slow to a crawl for something that should be lightweight (rendering many similar things).

## Pattern
**Flyweight**: minimize memory usage by **sharing** as much data as possible between similar objects, separating shared ("intrinsic") state from unique ("extrinsic") state — intrinsic state is stored once and shared; extrinsic state is passed in from outside each time it's needed.

Key ideas:
- **Intrinsic state** (shared, context-independent: texture, type, color) lives inside the Flyweight object and is reused.
- **Extrinsic state** (unique per instance: position) is kept *outside* the Flyweight and supplied by the client when needed.
- A **Flyweight Factory** ensures identical intrinsic-state objects are created once and reused, never duplicated.

## Java Implementation
```java
// Flyweight — holds only shared (intrinsic) state
public class TreeType {
    private final String name;
    private final String color;
    private final byte[] textureData; // heavy, shared

    public TreeType(String name, String color, byte[] textureData) {
        this.name = name;
        this.color = color;
        this.textureData = textureData;
    }

    public void draw(int x, int y) { // extrinsic state (x, y) passed in, not stored
        System.out.println("Drawing " + name + " (" + color + ") at [" + x + "," + y + "]");
    }
}
```

```java
// Flyweight Factory — ensures each unique TreeType is created only once
public class TreeTypeFactory {
    private static final Map<String, TreeType> cache = new HashMap<>();

    public static TreeType get(String name, String color, byte[] textureData) {
        String key = name + color;
        return cache.computeIfAbsent(key, k -> new TreeType(name, color, textureData));
    }
}
```

```java
// Context object — stores only extrinsic (unique) state + a reference to the shared flyweight
public class Tree {
    private final int x, y;
    private final TreeType type; // shared reference, not a copy

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw() {
        type.draw(x, y);
    }
}
```

Usage:
```java
List<Tree> forest = new ArrayList<>();
byte[] oakTexture = loadTexture("oak.png"); // loaded ONCE

for (int i = 0; i < 1_000_000; i++) {
    TreeType oakType = TreeTypeFactory.get("Oak", "Green", oakTexture); // returns the SAME shared instance
    forest.add(new Tree(randomX(), randomY(), oakType));
}
// Only ONE TreeType("Oak", "Green", ...) object exists in memory,
// referenced by all 1,000,000 Tree objects — massive memory savings.
```

## Real Spring Example
Spring's **singleton-scoped beans** are a form of Flyweight at the framework level: stateless services (like a `PriceCalculator` or `TaxService`) are created **once** and shared across every request, instead of instantiating a new one per request — exactly the "share what's common, don't duplicate" idea:

```java
@Service // singleton by default — ONE shared instance, reused everywhere
public class TaxCalculator {

    // Stateless, shared "intrinsic" logic — no per-request data stored here
    public double calculateTax(double amount, String region) {
        return switch (region) {
            case "US" -> amount * 0.07;
            case "EU" -> amount * 0.20;
            default -> amount * 0.10;
        };
    }
}
```
```java
@Service
public class OrderService {

    private final TaxCalculator taxCalculator; // shared flyweight-like singleton

    public OrderService(TaxCalculator taxCalculator) {
        this.taxCalculator = taxCalculator;
    }

    public double total(double amount, String region) { // region/amount = extrinsic, passed per-call
        return amount + taxCalculator.calculateTax(amount, region);
    }
}
```
`TaxCalculator` holds no per-order state — every order passes in its own `amount`/`region` (extrinsic state) while the shared, stateless logic (intrinsic) lives in one reused instance. This is why Spring beans should generally be **stateless** — it's what makes safely sharing one instance across many requests possible, the same guarantee Flyweight relies on.

## When NOT to Use It
- When object counts are **small** — the complexity of splitting intrinsic/extrinsic state and managing a factory cache isn't worth it if you're only creating hundreds of objects, not millions.
- When objects **don't actually share much data** — if most fields genuinely vary per instance, there's little to extract as "intrinsic," and Flyweight won't save meaningful memory.
- When shared flyweight objects need to be **mutable** — since one instance is shared across many contexts, mutating it affects everyone using it; flyweights must stay immutable or thread-unsafe bugs follow.
- When the extra indirection (factory lookups, passing extrinsic state everywhere) makes code **harder to read** than the memory savings justify — profile first; don't apply Flyweight preemptively without a demonstrated memory problem.