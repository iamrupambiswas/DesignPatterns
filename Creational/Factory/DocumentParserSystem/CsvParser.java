package Creational.Factory.DocumentParserSystem;

public class CsvParser implements DocumentParser {
    @Override
    public void parse(byte[] data) {
        System.out.println("Extracted text content from CSV file!");
    }
}
