package Creational.Factory.NotificationSystem;

public class NotificationFactory {
    public Notification createNotification(String type) {
        if(type.equals("SMS")) {
            return new SmsNotification();
        }
        if(type.equals("Email")) {
            return new EmailNotification();
        }
        
        throw new IllegalArgumentException("Unkown type!");
    }
}
