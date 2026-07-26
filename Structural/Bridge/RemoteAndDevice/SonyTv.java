package Structural.Bridge.RemoteAndDevice;

public class SonyTv extends Device {
    public SonyTv(Remote remote) {
        super(remote);
    }

    @Override
    public void play() {
        System.out.println("Playing sony TV!");
        remote.use();
    }
}
