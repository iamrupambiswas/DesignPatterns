package Creational.Factory.PaymentSystem;

public class CreditCardPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println(amount + " paid by credit card!");
    }
}
