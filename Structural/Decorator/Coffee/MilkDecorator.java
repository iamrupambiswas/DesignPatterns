package Structural.Decorator.Coffee;

public class MilkDecorator extends BeverageDecorator {
    
    public MilkDecorator(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double cost() {
        return beverage.cost() + 20;
    }

    @Override
    public String description() {
        return beverage.description() + " with Milk";
    }
}
