# Factory Pattern

## Problem
You need to create objects, but the **exact type to create depends on some condition** (input, config, environment) — e.g. sending a notification via `Email`, `SMS`, or `Push`, decided at runtime.

## Naive Solution
```java
public class NotificationService {

    public void send(String type, String message) {
        if (type.equals("EMAIL")) {
            EmailNotification n = new EmailNotification();
            n.send(message);
        } else if (type.equals("SMS")) {
            SmsNotification n = new SmsNotification();
            n.send(message);
        } else if (type.equals("PUSH")) {
            PushNotification n = new PushNotification();
            n.send(message);
        }
    }
}
```

## Why It Becomes Problematic
- **Object creation logic is scattered and duplicated** wherever a notification is needed — every caller repeats the same if/else.
- Violates the **Open/Closed Principle**: adding a new type (`WhatsAppNotification`) means editing this method (and every other place with similar logic) instead of just adding a new class.
- `NotificationService` is tightly coupled to every concrete class (`EmailNotification`, `SmsNotification`, ...) — hard to test, hard to swap implementations.
- Mixing "which object to create" with "what to do with it" makes the method do too much.

## Pattern
**Factory**: centralize object-creation logic in one place, so callers ask "give me an object that fits X" without knowing the concrete class being instantiated.

Key ideas:
- Define a common interface/abstract type for the products.
- A factory method (or class) decides which concrete implementation to instantiate.
- Client code depends only on the interface + the factory, never on concrete classes directly.

## Java Implementation
```java
public interface Notification {
    void send(String message);
}

public class EmailNotification implements Notification {
    public void send(String message) {
        System.out.println("Email sent: " + message);
    }
}

public class SmsNotification implements Notification {
    public void send(String message) {
        System.out.println("SMS sent: " + message);
    }
}

public class PushNotification implements Notification {
    public void send(String message) {
        System.out.println("Push sent: " + message);
    }
}
```

```java
public class NotificationFactory {

    public static Notification create(String type) {
        return switch (type) {
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SmsNotification();
            case "PUSH" -> new PushNotification();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}
```

Usage:
```java
Notification notification = NotificationFactory.create("EMAIL");
notification.send("Your order has shipped!");
```

Client code no longer knows or cares about `EmailNotification`, `SmsNotification`, etc. — it only depends on the `Notification` interface and the factory.

## Real Spring Example
Spring uses factories heavily — most obviously, the `ApplicationContext` itself is a factory of beans (`BeanFactory`). A common app-level pattern:

```java
public interface PaymentGateway {
    void pay(double amount);
}

@Component("stripeGateway")
public class StripeGateway implements PaymentGateway {
    public void pay(double amount) { /* ... */ }
}

@Component("paypalGateway")
public class PaypalGateway implements PaymentGateway {
    public void pay(double amount) { /* ... */ }
}
```
```java
@Service
public class PaymentGatewayFactory {

    private final Map<String, PaymentGateway> gateways;

    // Spring injects ALL PaymentGateway beans into a Map, keyed by bean name
    public PaymentGatewayFactory(Map<String, PaymentGateway> gateways) {
        this.gateways = gateways;
    }

    public PaymentGateway get(String provider) {
        PaymentGateway gateway = gateways.get(provider + "Gateway");
        if (gateway == null) {
            throw new IllegalArgumentException("Unknown provider: " + provider);
        }
        return gateway;
    }
}
```
```java
PaymentGateway gateway = paymentGatewayFactory.get("stripe");
gateway.pay(49.99);
```
This is a very common Spring idiom: let Spring autowire a `Map<String, Interface>` of all implementations, and pick the right one at runtime — no if/else chain, no `new` keyword anywhere in business code.

## When NOT to Use It
- When there's only **one implementation** and no real variation expected — a factory adds indirection with no benefit.
- When object creation is trivial (`new Foo()` with no branching logic) — just use `new` directly.
- When it turns into a **god factory** handling unrelated object families — split by responsibility instead (or consider Abstract Factory if you truly have families of related objects).
- In Spring apps, when plain **dependency injection** already gives you the right implementation via `@Qualifier` or `@Primary` — you don't always need a manual factory on top of the container.