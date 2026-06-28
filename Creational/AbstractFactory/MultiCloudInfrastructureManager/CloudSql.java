package Creational.AbstractFactory.MultiCloudInfrastructureManager;

public class CloudSql implements DatabaseInstance {

    @Override
    public void storeData() {
        System.out.println("Storing your data in GCP cloud sql!");
    }
    
}
