package Structural.Decorator.NotificationSystem;

public class Email implements Notification {
    @Override
    public void send() {
        System.out.println("Sending email notification");
    }
}
