package Creational.Factory.PaymentSystem;

public class UpiPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println(amount + " paid via UPI!");
    }
}
