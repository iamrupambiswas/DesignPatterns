# Facade Pattern

## Problem
You need to perform a task that involves **coordinating several complex subsystems** — e.g. placing an order requires checking inventory, charging payment, updating shipping, and sending a notification. Client code that needs to "just place an order" shouldn't have to know all these subsystems intimately.

## Naive Solution
```java
public class OrderController {

    public void placeOrder(String productId, int quantity, String customerId) {
        InventoryService inventory = new InventoryService();
        if (!inventory.checkStock(productId, quantity)) {
            throw new RuntimeException("Out of stock");
        }
        inventory.reserve(productId, quantity);

        PaymentService payment = new PaymentService();
        String transactionId = payment.charge(customerId, quantity * 10.0);

        ShippingService shipping = new ShippingService();
        shipping.schedule(productId, quantity, customerId);

        NotificationService notification = new NotificationService();
        notification.sendOrderConfirmation(customerId, transactionId);
    }
}
```

## Why It Becomes Problematic
- Every caller that needs to "place an order" has to know **all four subsystems**, their exact APIs, and the correct order of operations — this logic gets copy-pasted anywhere ordering happens (web controller, batch job, admin tool).
- `OrderController` is **tightly coupled** to every subsystem's concrete class — a change to `PaymentService`'s API ripples out to every caller.
- The orchestration logic (check stock → reserve → charge → ship → notify) is business-critical, but it's scattered across client code instead of living in one place.
- Hard to test in isolation — testing "does placing an order work" means dealing with four real subsystems at once, wherever this logic is duplicated.

## Pattern
**Facade**: provide a simplified, unified interface to a set of interfaces in a complex subsystem, making the subsystem easier to use without hiding its full power for callers who still need it directly.

Key ideas:
- One class (`OrderFacade`) exposes a small number of high-level methods (`placeOrder(...)`).
- Internally, the facade coordinates calls to the subsystem classes in the correct order.
- Client code depends only on the facade for the common case — subsystems remain accessible directly for callers who need finer control.

## Java Implementation
```java
public class InventoryService {
    public boolean checkStock(String productId, int qty) { return true; }
    public void reserve(String productId, int qty) { System.out.println("Reserved " + qty + " of " + productId); }
}

public class PaymentService {
    public String charge(String customerId, double amount) {
        System.out.println("Charged " + customerId + ": $" + amount);
        return "txn-123";
    }
}

public class ShippingService {
    public void schedule(String productId, int qty, String customerId) {
        System.out.println("Shipping scheduled for " + customerId);
    }
}

public class NotificationService {
    public void sendOrderConfirmation(String customerId, String transactionId) {
        System.out.println("Confirmation sent for " + transactionId);
    }
}
```

```java
// Facade — the simple, unified entry point
public class OrderFacade {

    private final InventoryService inventory = new InventoryService();
    private final PaymentService payment = new PaymentService();
    private final ShippingService shipping = new ShippingService();
    private final NotificationService notification = new NotificationService();

    public void placeOrder(String productId, int quantity, String customerId, double amount) {
        if (!inventory.checkStock(productId, quantity)) {
            throw new IllegalStateException("Out of stock");
        }
        inventory.reserve(productId, quantity);
        String transactionId = payment.charge(customerId, amount);
        shipping.schedule(productId, quantity, customerId);
        notification.sendOrderConfirmation(customerId, transactionId);
    }
}
```

Usage:
```java
OrderFacade orderFacade = new OrderFacade();
orderFacade.placeOrder("SKU-001", 2, "cust-42", 20.0);
// One call — all coordination logic lives in ONE place
```

## Real Spring Example
`JdbcTemplate` itself is a Facade over raw JDBC — it hides `Connection`, `PreparedStatement`, `ResultSet` setup/teardown/exception-handling behind simple methods like `queryForObject(...)`. At the application level, a **Service** class commonly acts as a facade over several repositories/clients:

```java
@Service
public class OrderFacadeService {

    private final InventoryRepository inventoryRepository;
    private final PaymentClient paymentClient;
    private final ShippingService shippingService;
    private final NotificationService notificationService;

    public OrderFacadeService(InventoryRepository inventoryRepository,
                               PaymentClient paymentClient,
                               ShippingService shippingService,
                               NotificationService notificationService) {
        this.inventoryRepository = inventoryRepository;
        this.paymentClient = paymentClient;
        this.shippingService = shippingService;
        this.notificationService = notificationService;
    }

    @Transactional
    public OrderResult placeOrder(OrderRequest request) {
        inventoryRepository.reserve(request.productId(), request.quantity());
        String txnId = paymentClient.charge(request.customerId(), request.amount());
        shippingService.schedule(request);
        notificationService.sendConfirmation(request.customerId(), txnId);
        return new OrderResult(txnId);
    }
}
```
```java
@RestController
public class OrderController {

    private final OrderFacadeService orderFacadeService;

    public OrderController(OrderFacadeService orderFacadeService) {
        this.orderFacadeService = orderFacadeService;
    }

    @PostMapping("/orders")
    public OrderResult placeOrder(@RequestBody OrderRequest request) {
        return orderFacadeService.placeOrder(request); // controller stays thin
    }
}
```
The controller doesn't know `InventoryRepository`, `PaymentClient`, `ShippingService`, or `NotificationService` exist — it just calls one method on the facade, exactly the simplification Facade is meant to provide.

## When NOT to Use It
- When the subsystem is **already simple** — wrapping a single class with another class that just delegates every call adds a pointless layer.
- When different callers need **fine-grained control** over subsystem interactions in ways the facade doesn't expose — forcing everything through one rigid high-level method can become limiting; keep subsystem classes accessible directly for those cases.
- When the facade starts accumulating **unrelated responsibilities** just because "it's the entry point" — this turns it into a god object; keep it focused purely on orchestration/coordination.
- When over-hiding the subsystem makes debugging harder — if consumers never see what's actually happening underneath, diagnosing issues in the coordinated flow can become more difficult.