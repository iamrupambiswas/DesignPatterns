public class Main {
    public static void main(String[] args) {
        OrderControllerWithoutFacade orderController = new OrderControllerWithoutFacade();
        orderController.placeOrder("product123", 100.0);

        OrderControllerWithFacade orderControllerWithFacade = new OrderControllerWithFacade();
        orderControllerWithFacade.placeOrder("product456", 200.0);
    }
}
