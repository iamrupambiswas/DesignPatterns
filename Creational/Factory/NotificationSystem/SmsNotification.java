package Creational.Factory.NotificationSystem;

public class SmsNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Sms sent!");
    }
}
