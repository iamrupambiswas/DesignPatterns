package Structural.Decorator.NotificationSystem;

public class SmsDecorator extends NotificationDecorator {

    public SmsDecorator(Notification notification) {
        super(notification);
    }

    @Override
    public void send() {
        notification.send();
        System.out.println("Sending SMS notification");
    }
    
}
