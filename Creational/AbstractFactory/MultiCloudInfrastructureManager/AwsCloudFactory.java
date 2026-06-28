package Creational.AbstractFactory.MultiCloudInfrastructureManager;

public class AwsCloudFactory implements CloudFactory{

    @Override
    public ComputerInstance getComputerInstance() {
        return new Ec2();
    }

    @Override
    public BlobStorage storeBlobStorage() {
        return new S3();
    }

    @Override
    public DatabaseInstance getDbInstance() {
        return new Rds();
    }
    
}
