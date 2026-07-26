package Structural.Bridge.RemoteAndDevice;

public abstract class Device {
    protected Remote remote;

    public Device(Remote remote) {
        this.remote = remote;
    }

    abstract void play();
}
