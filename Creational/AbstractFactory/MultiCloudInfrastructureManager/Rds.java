package Creational.AbstractFactory.MultiCloudInfrastructureManager;

public class Rds implements DatabaseInstance {

    @Override
    public void storeData() {
        System.out.println("Storing your data in AWS RDS!");
    }
    
}
