package Creational.Factory;

import Creational.Factory.DocumentParserSystem.DocumentParser;
import Creational.Factory.DocumentParserSystem.DocumentParserFactory;
import Creational.Factory.NotificationSystem.Notification;
import Creational.Factory.NotificationSystem.NotificationFactory;
import Creational.Factory.PaymentSystem.Payment;
import Creational.Factory.PaymentSystem.PaymentFactory;

public class Main {
    public static void main(String[] args) {

        // NotificationFactory factory = new NotificationFactory();
        // Notification note1 = factory.createNotification("Email");
        // note1.send("Hello World!");

        // PaymentFactory paymentFactory = new PaymentFactory();
        // Payment upiPayment = paymentFactory.createPayment("UPI");
        // upiPayment.pay(1500);
        // Payment creditCardPayment = paymentFactory.createPayment("Credit Card");
        // creditCardPayment.pay(2500);

        DocumentParserFactory documentParserFactory = new DocumentParserFactory();
        DocumentParser pdfParser = documentParserFactory.createParser("PDF");
        pdfParser.parse(null);
        DocumentParser csvParser = documentParserFactory.createParser("CSV");
        csvParser.parse(null);
    }
}
