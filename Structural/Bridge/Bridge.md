# Bridge Pattern

## Problem
You have a class hierarchy with **two independent dimensions** that both vary — e.g. `Shape` (Circle, Square) that can be rendered in different ways (`VectorRenderer`, `RasterRenderer`), or `Notification` type (Email, SMS) combined with `Priority` level. You want to combine them freely without an explosion of subclasses.

## Naive Solution
```java
public class VectorCircle extends Shape { ... }
public class RasterCircle extends Shape { ... }
public class VectorSquare extends Shape { ... }
public class RasterSquare extends Shape { ... }
```
Every new shape × every new renderer = a new class.

## Why It Becomes Problematic
- **Combinatorial explosion**: 2 shapes × 2 renderers = 4 classes. Add a `Triangle` and a `GpuRenderer` → 3 × 3 = 9 classes. It grows multiplicatively, not additively.
- The two concerns (**what shape** vs **how it's drawn**) are tangled into a single inheritance chain — you can't change rendering strategy without touching every shape subclass.
- Duplicated logic: `VectorCircle` and `VectorSquare` likely share rendering setup code that gets copy-pasted instead of reused.
- Adding a feature to "how it's rendered" (e.g. anti-aliasing) means editing every single leaf subclass.

## Pattern
**Bridge**: decouple an abstraction from its implementation so the two can vary independently — split one bloated hierarchy into two smaller, separate hierarchies connected by composition (a "bridge") instead of inheritance.

Key ideas:
- The **abstraction** (`Shape`) holds a reference to an **implementor** interface (`Renderer`), instead of extending a combined subclass.
- Concrete abstractions (`Circle`, `Square`) and concrete implementors (`VectorRenderer`, `RasterRenderer`) vary independently.
- Any abstraction can be paired with any implementor at runtime — combinations become `N + M` classes instead of `N × M`.

## Java Implementation
```java
// Implementor hierarchy — "how it's drawn"
public interface Renderer {
    void renderCircle(float radius);
    void renderSquare(float side);
}

public class VectorRenderer implements Renderer {
    public void renderCircle(float radius) {
        System.out.println("Drawing circle as vector, radius " + radius);
    }
    public void renderSquare(float side) {
        System.out.println("Drawing square as vector, side " + side);
    }
}

public class RasterRenderer implements Renderer {
    public void renderCircle(float radius) {
        System.out.println("Drawing circle as pixels, radius " + radius);
    }
    public void renderSquare(float side) {
        System.out.println("Drawing square as pixels, side " + side);
    }
}
```

```java
// Abstraction hierarchy — "what shape it is"
public abstract class Shape {
    protected Renderer renderer; // the "bridge"

    protected Shape(Renderer renderer) {
        this.renderer = renderer;
    }

    public abstract void draw();
}

public class Circle extends Shape {
    private final float radius;

    public Circle(Renderer renderer, float radius) {
        super(renderer);
        this.radius = radius;
    }

    public void draw() {
        renderer.renderCircle(radius);
    }
}

public class Square extends Shape {
    private final float side;

    public Square(Renderer renderer, float side) {
        super(renderer);
        this.side = side;
    }

    public void draw() {
        renderer.renderSquare(side);
    }
}
```

Usage:
```java
Shape vectorCircle = new Circle(new VectorRenderer(), 5.0f);
Shape rasterSquare = new Square(new RasterRenderer(), 3.0f);

vectorCircle.draw(); // Drawing circle as vector, radius 5.0
rasterSquare.draw(); // Drawing square as pixels, side 3.0

// Add a GpuRenderer later? Just implement Renderer — zero new Shape classes needed.
```

## Real Spring Example
Spring's `JdbcTemplate` + `DataSource` split reflects a Bridge-like separation: `JdbcTemplate` (the abstraction — "how you query") is decoupled from the actual database connection mechanism (`DataSource` — the implementor), and either side can vary independently. A more explicit application-level example:

```java
// Implementor — "how messages are actually delivered"
public interface MessageSender {
    void sendRaw(String recipient, String content);
}

@Component
public class SmtpSender implements MessageSender {
    public void sendRaw(String recipient, String content) {
        System.out.println("SMTP -> " + recipient + ": " + content);
    }
}

@Component
public class TwilioSender implements MessageSender {
    public void sendRaw(String recipient, String content) {
        System.out.println("Twilio -> " + recipient + ": " + content);
    }
}
```
```java
// Abstraction — "what kind of notification it is"
public abstract class Notification {
    protected final MessageSender sender; // the bridge

    protected Notification(MessageSender sender) {
        this.sender = sender;
    }

    public abstract void notify(String recipient, String message);
}

@Component
public class UrgentNotification extends Notification {
    public UrgentNotification(SmtpSender sender) { super(sender); }

    public void notify(String recipient, String message) {
        sender.sendRaw(recipient, "[URGENT] " + message);
    }
}
```
The delivery mechanism (`SmtpSender` vs `TwilioSender`) and the notification type (`UrgentNotification` vs a future `ReminderNotification`) evolve independently, and Spring's DI wires whichever combination is needed — no combinatorial subclass explosion.

## When NOT to Use It
- When you only have **one dimension of variation**, not two — plain inheritance or Strategy is simpler; Bridge solves a problem you don't have yet.
- When the hierarchy is small and unlikely to grow — the extra indirection (abstraction + implementor interfaces) adds complexity without a real payoff.
- When introducing Bridge speculatively "in case we need it later" — this is over-engineering; add the bridge when the second dimension of variation actually appears, not before.
- When the two "dimensions" aren't really independent — if changing one always requires changing the other in lockstep, they're not separate axes and don't need decoupling.