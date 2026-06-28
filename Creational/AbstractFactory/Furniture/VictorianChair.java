package Creational.AbstractFactory.Furniture;

public class VictorianChair implements Chair{
    @Override
    public void sitOn() {
        System.out.println("Sitting on an ornate velvet chair!");
    }
}
