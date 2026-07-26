public class PaymentAdapter implements PaymentProcessor {
    private OldPayService oldPayService;

    public PaymentAdapter(OldPayService oldPayService) {
        this.oldPayService = oldPayService;
    }

    @Override
    public void processPayment(double amount) {
        oldPayService.makePayment(amount);
    }
}
