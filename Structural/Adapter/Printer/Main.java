package Structural.Printer;

public class Main {
    public static void main(String[] args) {
        LegacyPrinter legacyPrinter = new LegacyPrinter();
        PrinterAdapter printerAdapter = new PrinterAdapter(legacyPrinter);
        printerAdapter.print();
    }
}
