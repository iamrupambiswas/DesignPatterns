package Structural.Decorator.Coffee;

public class SugarDecorator extends BeverageDecorator {
    
    public SugarDecorator(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double cost() {
        return beverage.cost() + 10;
    }

    @Override
    public String description() {
        return beverage.description() + " with Sugar";
    }
    
}
