package Creational.AbstractFactory.MultiCloudInfrastructureManager;

public class S3 implements BlobStorage {

    @Override
    public void storeObject() {
        System.out.println("Storing the object in AWS S3 object storage!");
    }
    
}
