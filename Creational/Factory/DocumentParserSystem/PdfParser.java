package Creational.Factory.DocumentParserSystem;

public class PdfParser implements DocumentParser {
    @Override
    public void parse(byte[] data) {
        System.out.println("Extracted text content from PDF file!");
    }
}
