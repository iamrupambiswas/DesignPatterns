package Structural.Decorator.NotificationSystem;

public class Main {
    public static void main(String[] args) {
        Notification emailNotification = new Email();
        emailNotification = new WhatsappDecorator(emailNotification);
        emailNotification = new SmsDecorator(emailNotification);

        emailNotification.send();
    }
}
