package Creational.AbstractFactory.MultiCloudInfrastructureManager;

class Application {
    private ComputerInstance vm;
    private BlobStorage objecStorage;
    private DatabaseInstance database;

    public Application(CloudFactory factory) {
        vm = factory.getComputerInstance();
        objecStorage = factory.storeBlobStorage();
        database = factory.getDbInstance();
    }

    public void getVm() {
        vm.getInstance();
    }

    public void useObjectStorage() {
        objecStorage.storeObject();
    }

    public void useDatabase() {
        database.storeData();
    }
}

public class Main {
    public static void main(String[] args) {
        CloudFactory awsFactory = new AwsCloudFactory();
        Application awsApplication = new Application(awsFactory);
        awsApplication.getVm();
        awsApplication.useObjectStorage();
        awsApplication.useDatabase();

        CloudFactory gcpFactory = new GcpCloudFactory();
        Application gcpApplication = new Application(gcpFactory);
        gcpApplication.getVm();
        gcpApplication.useObjectStorage();
        gcpApplication.useDatabase();
        
    }
}
