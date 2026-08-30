package Structural.Facade.HomeTheatre;

public class StreamingBox {
    public void turnOn() {
        System.out.println("Streaming Box: Turning on...");
    }
    public void playMovie(String title) {
        System.out.println("Streaming Box: Playing '" + title + "'");
    }
}
