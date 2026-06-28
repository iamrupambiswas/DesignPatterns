package Creational.AbstractFactory.MultiCloudInfrastructureManager;

public class CloudStorage implements BlobStorage {

    @Override
    public void storeObject() {
        System.out.println("Storing the object in GCP Cloud Storage!");
    }
    
}
