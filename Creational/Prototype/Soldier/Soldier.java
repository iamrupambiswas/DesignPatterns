package Creational.Prototype.Soldier;

import Creational.Prototype.Prototype;

public class Soldier implements Prototype<Soldier> {

    String name;
    int health;
    int attack;

    public Soldier(String name, int health, int attack) {
        this.name = name;
        this.health = health;
        this.attack = attack;
    }

    @Override
    public Soldier clone() {
        return new Soldier(this.name, this.health, this.attack);
    }

    void display() {
        System.out.println("Soldier Name: " + name);
        System.out.println("Health: " + health);
        System.out.println("Attack: " + attack);
    }
}
