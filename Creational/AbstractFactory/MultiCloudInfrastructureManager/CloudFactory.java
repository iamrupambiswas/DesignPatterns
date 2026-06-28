package Creational.AbstractFactory.MultiCloudInfrastructureManager;

public interface CloudFactory {
    ComputerInstance getComputerInstance();
    BlobStorage storeBlobStorage();
    DatabaseInstance getDbInstance();
}
