public class OrderControllerWithFacade {
    private OrderFacade orderFacade;

    public OrderControllerWithFacade() {
        this.orderFacade = new OrderFacade();
    }

    public void placeOrder(String productId, double amount) {
        orderFacade.placeOrder(productId, amount);
    }
}
