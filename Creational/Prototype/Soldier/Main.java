package Creational.Prototype.Soldier;

public class Main {
    public static void main(String args[]) {
        Soldier prototype = new Soldier("Prototype", 100, 20);

        Soldier enemy1 = prototype.clone();
        enemy1.name = "Enemy 1";

        Soldier enemy2 = prototype.clone();
        enemy2.name = "Enemy 2";

        prototype.display();
        enemy1.display();
        enemy2.display();
    }
}
