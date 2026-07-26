public class Main {
    public static void main(String[] args) {
        Shape circle = new Circle(new RedColor());
        Shape square = new Square(new BlueColor());

        circle.draw();
        square.draw();
    }
}
