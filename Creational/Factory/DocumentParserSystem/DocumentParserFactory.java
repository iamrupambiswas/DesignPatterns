package Creational.Factory.DocumentParserSystem;

public class DocumentParserFactory {
    public DocumentParser createParser(String type) {
        if(type.equals("PDF")) {
            return new PdfParser();
        }
        if(type.equals("CSV")) {
            return new CsvParser();
        }
        throw new IllegalArgumentException("Invalid type!");
    }
}
