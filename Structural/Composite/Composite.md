# Composite Pattern

## Problem
You need to represent **part-whole hierarchies** — e.g. a file system with `File`s and `Folder`s (folders contain files and other folders), or an org chart with employees and managers who manage other employees. You want to treat individual objects and groups of objects **the same way** (e.g. "calculate total size" should work whether it's a single file or an entire folder tree).

## Naive Solution
```java
public class File {
    private String name;
    private long size;

    public long getSize() { return size; }
}

public class Folder {
    private String name;
    private List<File> files = new ArrayList<>();
    private List<Folder> subFolders = new ArrayList<>();

    public long getTotalSize() {
        long total = 0;
        for (File f : files) total += f.getSize();
        for (Folder sub : subFolders) total += sub.getTotalSize(); // manual recursion
        return total;
    }
}
```

## Why It Becomes Problematic
- Client code has to **know the difference** between a `File` and a `Folder` and handle each with separate logic/type checks (`if (item instanceof File) ... else if (item instanceof Folder) ...`).
- Every new operation (`getTotalSize`, `print`, `search`, `delete`) has to be implemented **twice** — once for `File`, once for `Folder` — with manual recursion glue code duplicated in each.
- Adding a third kind of node (e.g. `SymbolicLink`) means touching every operation across both existing classes.
- The recursive "walk the tree" logic gets scattered instead of living in one place.

## Pattern
**Composite**: compose objects into tree structures to represent part-whole hierarchies, so clients can treat **individual objects and compositions of objects uniformly** through a single common interface.

Key ideas:
- Define a common interface (`FileSystemItem`) that both leaf objects (`File`) and container objects (`Folder`) implement.
- The container (`Folder`) holds a list of child `FileSystemItem`s (which may themselves be leaves or containers) and implements operations by **delegating to its children recursively**.
- Client code calls the same method on anything — a single file or an entire tree — without caring which it is.

## Java Implementation
```java
// Common interface — leaf and composite both implement this
public interface FileSystemItem {
    long getSize();
    void print(String indent);
}

// Leaf
public class File implements FileSystemItem {
    private final String name;
    private final long size;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "- " + name + " (" + size + "b)");
    }
}

// Composite
public class Folder implements FileSystemItem {
    private final String name;
    private final List<FileSystemItem> children = new ArrayList<>();

    public Folder(String name) {
        this.name = name;
    }

    public void add(FileSystemItem item) {
        children.add(item);
    }

    @Override
    public long getSize() {
        long total = 0;
        for (FileSystemItem child : children) {
            total += child.getSize(); // works for File OR Folder, no type check needed
        }
        return total;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "+ " + name);
        for (FileSystemItem child : children) {
            child.print(indent + "  ");
        }
    }
}
```

Usage:
```java
Folder root = new Folder("project");
Folder src = new Folder("src");
src.add(new File("Main.java", 1200));
src.add(new File("Utils.java", 800));

Folder docs = new Folder("docs");
docs.add(new File("README.md", 300));

root.add(src);
root.add(docs);
root.add(new File(".gitignore", 50));

System.out.println("Total size: " + root.getSize()); // recursively sums everything
root.print("");
```
Client code never distinguishes `File` from `Folder` — it just calls `getSize()` or `print()` on a `FileSystemItem`, and the recursion happens naturally through the tree.

## Real Spring Example
Spring Security's `AccessDecisionManager` (and its voters) is a practical near-Composite: a `UnanimousBased`/`AffirmativeBased` decision manager holds a list of `AccessDecisionVoter`s and delegates the decision, treating "one voter" and "a collection of voters" through the same evaluation flow. A clearer application-level example — a **composite validator**:

```java
public interface Validator<T> {
    List<String> validate(T input);
}

@Component
public class NotBlankValidator implements Validator<Order> {
    public List<String> validate(Order order) {
        return order.getCustomerName().isBlank()
                ? List.of("Customer name is required")
                : List.of();
    }
}

@Component
public class PositiveAmountValidator implements Validator<Order> {
    public List<String> validate(Order order) {
        return order.getAmount() <= 0
                ? List.of("Amount must be positive")
                : List.of();
    }
}
```
```java
@Component
public class CompositeOrderValidator implements Validator<Order> {

    private final List<Validator<Order>> validators;

    // Spring injects EVERY Validator<Order> bean into this list automatically
    public CompositeOrderValidator(List<Validator<Order>> validators) {
        this.validators = validators;
    }

    @Override
    public List<String> validate(Order order) {
        return validators.stream()
                .flatMap(v -> v.validate(order).stream())
                .toList();
    }
}
```
Callers just call `validate()` on the `CompositeOrderValidator` exactly like they would on any single `Validator<Order>` — new validators can be added by just declaring a new `@Component`, with zero changes to calling code.

## When NOT to Use It
- When the hierarchy is **shallow and fixed** (never more than one level, never truly recursive) — a simple `List` and a loop is clearer than a Composite structure.
- When leaf and container nodes have **very different capabilities** that can't be meaningfully unified behind one interface — forcing them into a shared interface leads to awkward no-op or exception-throwing methods (e.g. `add()` on a `File` that must throw `UnsupportedOperationException`).
- When operations differ significantly between leaves and composites — if most methods need `instanceof` checks anyway, the "uniform treatment" benefit is lost and Composite adds overhead for nothing.
- When performance matters and deep recursive trees could cause stack issues — very deep composites may need iterative traversal instead of naive recursion.