package Structural.Decorator.Coffee;

public class Coffee implements Beverage {
    @Override
    public double cost() {
        return 100;
    }

    @Override
    public String description() {
        return "Coffee";
    }
}
