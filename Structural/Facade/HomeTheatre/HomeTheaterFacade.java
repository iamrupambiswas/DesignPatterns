package Structural.Facade.HomeTheatre;

class HomeTheaterFacade {
    private final Projector projector;
    private final SoundSystem sound;
    private final StreamingBox player;

    public HomeTheaterFacade(Projector projector, SoundSystem sound, StreamingBox player) {
        this.projector = projector;
        this.sound = sound;
        this.player = player;
    }

    public void watchMovie(String movieTitle) {
        System.out.println("--- Starting Movie Mode ---");
        projector.turnOn();
        projector.setInput("HDMI 1");
        sound.turnOn();
        sound.setVolume(15);
        player.turnOn();
        player.playMovie(movieTitle);
        System.out.println("--- Enjoy your movie! ---\n");
    }
}
