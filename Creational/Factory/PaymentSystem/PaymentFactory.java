package Creational.Factory.PaymentSystem;

public class PaymentFactory {
    public Payment createPayment(String paymentMethod) {
        if(paymentMethod.equals("UPI")) {
            return new UpiPayment();
        }
        if(paymentMethod.equals("Credit Card")) {
            return new CreditCardPayment();
        }
        throw new IllegalArgumentException("Unkown method!");
    }
}
