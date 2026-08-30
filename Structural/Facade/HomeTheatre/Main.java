package Structural.Facade.HomeTheatre;

public class Main {
    public static void main(String[] args) {
        Projector projector = new Projector();
        SoundSystem sound = new SoundSystem();
        StreamingBox player = new StreamingBox();

        HomeTheaterFacade homeTheater = new HomeTheaterFacade(projector, sound, player);
        homeTheater.watchMovie("Inception");
    }
}
