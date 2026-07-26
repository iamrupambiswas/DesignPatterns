package Structural.Bridge.RemoteAndDevice;

public class SamsungTv extends Device {
    public SamsungTv(Remote remote) {
        super(remote);
    }

    @Override
    public void play() {
        System.out.println("Playing Samsung TV!");
        remote.use();
    }
}
