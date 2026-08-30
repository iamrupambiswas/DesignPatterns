package Structural.Decorator.Coffee;

public class Main {
    public static void main(String[] args) {
        Beverage coffee = new Coffee();
        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);
        
        System.out.println(coffee.description() + " costs $" + coffee.cost());
    }
}
