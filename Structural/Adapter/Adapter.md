# Adapter Pattern

## Problem
You have an existing class (yours, or a third-party library) with an **incompatible interface** from what your code expects — e.g. your app expects a `PaymentProcessor` interface, but the payment library gives you a `LegacyPaymentGateway` with completely different method names/signatures. You can't change the library's code, and you don't want to rewrite your app around it.

## Naive Solution
```java
public class OrderService {

    private final LegacyPaymentGateway legacyGateway = new LegacyPaymentGateway();

    public void checkout(double amount) {
        // forced to use the library's awkward API directly, everywhere
        legacyGateway.initTransaction();
        legacyGateway.setAmountInCents((int) (amount * 100));
        legacyGateway.executePayment();
    }
}
```

## Why It Becomes Problematic
- `OrderService` is now **tightly coupled** to `LegacyPaymentGateway`'s specific, awkward API (cents instead of dollars, multi-step init/execute calls).
- If you ever swap payment providers, or want to support multiple ones, you have to rewrite every place that calls the legacy API directly.
- Business logic gets polluted with **adaptation code** (unit conversions, step ordering) mixed in with actual order logic.
- Hard to unit test `OrderService` in isolation — you can't easily mock `LegacyPaymentGateway`'s clunky, stateful API.

## Pattern
**Adapter**: convert the interface of a class into another interface clients expect, letting incompatible classes work together without modifying either side.

Key ideas:
- Define the interface your code actually wants to use (the "target").
- Write an Adapter class that implements the target interface and internally translates calls to the incompatible class (the "adaptee").
- Client code depends only on the target interface — never touches the adaptee directly.

## Java Implementation
```java
// The interface your application actually wants
public interface PaymentProcessor {
    void pay(double amountInDollars);
}

// The incompatible third-party/legacy class you can't change
public class LegacyPaymentGateway {
    public void initTransaction() { System.out.println("Transaction initialized"); }
    public void setAmountInCents(int cents) { System.out.println("Amount set: " + cents + " cents"); }
    public void executePayment() { System.out.println("Payment executed"); }
}
```

```java
// Adapter bridges the gap
public class LegacyPaymentAdapter implements PaymentProcessor {

    private final LegacyPaymentGateway legacyGateway;

    public LegacyPaymentAdapter(LegacyPaymentGateway legacyGateway) {
        this.legacyGateway = legacyGateway;
    }

    @Override
    public void pay(double amountInDollars) {
        legacyGateway.initTransaction();
        legacyGateway.setAmountInCents((int) Math.round(amountInDollars * 100));
        legacyGateway.executePayment();
    }
}
```

Usage:
```java
PaymentProcessor processor = new LegacyPaymentAdapter(new LegacyPaymentGateway());

public class OrderService {
    private final PaymentProcessor processor;

    public OrderService(PaymentProcessor processor) {
        this.processor = processor;
    }

    public void checkout(double amount) {
        processor.pay(amount); // clean, simple, decoupled from legacy details
    }
}
```
`OrderService` now only knows about `PaymentProcessor` — swapping to Stripe, PayPal, or any other provider just means writing a new adapter, with zero changes to business logic.

## Real Spring Example
Spring's own `HandlerAdapter` is a textbook example — it lets `DispatcherServlet` invoke wildly different types of controllers (`@Controller` methods, `HttpRequestHandler`, legacy `Controller` interface implementations) through one uniform interface:

```java
public interface HandlerAdapter {
    boolean supports(Object handler);
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
}
```
`DispatcherServlet` doesn't know or care whether a handler is an annotated `@Controller` method or an old-style `Controller` implementation — it just asks each registered `HandlerAdapter`: "do you support this?" and delegates accordingly.

A more everyday example: adapting a third-party SMS library into your app's own interface:
```java
public interface SmsSender {
    void send(String phoneNumber, String message);
}

@Component
public class TwilioSmsAdapter implements SmsSender {

    private final TwilioClient twilioClient; // third-party class, incompatible API

    public TwilioSmsAdapter(TwilioClient twilioClient) {
        this.twilioClient = twilioClient;
    }

    @Override
    public void send(String phoneNumber, String message) {
        twilioClient.buildMessage()
                .to(phoneNumber)
                .body(message)
                .dispatch(); // Twilio's own fluent API, hidden behind our interface
    }
}
```
```java
@Service
public class NotificationService {
    private final SmsSender smsSender; // depends only on OUR interface

    public NotificationService(SmsSender smsSender) {
        this.smsSender = smsSender;
    }
}
```

## When NOT to Use It
- When you **own both sides** of the interface mismatch — just change one of them to match, instead of adding an adapter layer.
- When there's only **one implementation ever**, and no real chance of swapping providers — the adapter is speculative abstraction with no payoff.
- When the interfaces are **almost identical already** — a thin adapter that just renames one method isn't adding value, it's noise.
- When adaptation logic is complex enough that it hides **real behavioral differences** (not just interface shape) — silently adapting can mask bugs; sometimes it's better to surface the difference explicitly than paper over it.