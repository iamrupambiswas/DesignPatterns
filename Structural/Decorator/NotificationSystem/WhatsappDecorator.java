package Structural.Decorator.NotificationSystem;

public class WhatsappDecorator extends NotificationDecorator {
    
    public WhatsappDecorator(Notification notification) {
        super(notification);
    }

    @Override
    public void send() {
        notification.send();
        System.out.println("Sending WhatsApp notification");
    }
}
