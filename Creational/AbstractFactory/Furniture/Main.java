package Creational.AbstractFactory.Furniture;

class Application {
    private Chair chair;
    private Sofa sofa;

    public Application(FurnitureFactory factory) {
        this.chair = factory.createChair();
        this.sofa = factory.createSofa();
    }

    public void testFurniture() {
        chair.sitOn();
        sofa.loungeOn();
    }
}

public class Main {
    public static void main(String[] args) {

        // FurnitureFactory modernBrand = new ModernFurnitureFactory();
        // Application app1 = new Application(modernBrand);
        // app1.testFurniture();

        FurnitureFactory heritageBrand = new VictorianFurnitureFactory();
        Application app2 = new Application(heritageBrand);
        app2.testFurniture();
    }
}
