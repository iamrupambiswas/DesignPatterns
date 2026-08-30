public class OrderControllerWithoutFacade {
    public void placeOrder(String productId, double amount) {

        InventoryService inventory = new InventoryService();
        PaymentService payment = new PaymentService();
        ShippingService shipping = new ShippingService();
        NotificationService notification = new NotificationService();

        if (!inventory.checkStock(productId)) {
            return;
        }

        if (!payment.processPayment(amount)) {
            return;
        }

        shipping.createShipment(productId);

        notification.sendNotification("Order placed successfully");
    }
}
