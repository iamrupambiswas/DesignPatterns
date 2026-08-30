package Structural.Facade.HomeTheatre;

public class Projector {
    public void turnOn() {
        System.out.println("Projector: Turning on...");
    }
    public void setInput(String source) {
        System.out.println("Projector: Input set to " + source);
    }
}
