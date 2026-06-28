package Creational.AbstractFactory.MultiCloudInfrastructureManager;

public class ComputeEngine implements ComputerInstance {

    @Override
    public void getInstance() {
        System.out.println("You got an GCP computer instance!");
    }
    
}
