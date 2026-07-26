package Structural.Bridge.RemoteAndDevice;

public class Main {
    public static void main(String[] args) {
        Device sonyTv = new SonyTv(new BasicRemote());
        // Device sonyTv = new SonyTv(new SmartRemote());

        Device samsunTv = new SamsungTv(new SmartRemote());
        // Device samsunTv = new SamsungTv(new BasicRemote());

        sonyTv.play();
        samsunTv.play();
    }
}
