public class OrderFacade {
    private InventoryService inventoryService;
    private PaymentService paymentService;
    private ShippingService shippingService;
    private NotificationService notificationService;

    public OrderFacade() {
        this.inventoryService = new InventoryService();
        this.paymentService = new PaymentService();
        this.shippingService = new ShippingService();
        this.notificationService = new NotificationService();
    }

    public void placeOrder(String productId, double amount) {
        if (!inventoryService.checkStock(productId)) {
            return;
        }

        if (!paymentService.processPayment(amount)) {
            return;
        }

        shippingService.createShipment(productId);

        notificationService.sendNotification("Order placed successfully");
    }
}
