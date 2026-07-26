public class Main {
    public static void main(String[] args) {
        OldPayService oldPayService = new OldPayService();
        PaymentProcessor paymentProcessor = new PaymentAdapter(oldPayService);
        paymentProcessor.processPayment(100.0);
    }
}
