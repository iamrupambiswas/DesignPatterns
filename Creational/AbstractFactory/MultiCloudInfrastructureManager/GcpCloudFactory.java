package Creational.AbstractFactory.MultiCloudInfrastructureManager;

public class GcpCloudFactory implements CloudFactory {

    @Override
    public ComputerInstance getComputerInstance() {
        return new ComputeEngine();
    }

    @Override
    public BlobStorage storeBlobStorage() {
        return new CloudStorage();
    }

    @Override
    public DatabaseInstance getDbInstance() {
        return new CloudSql();
    }
    
}
