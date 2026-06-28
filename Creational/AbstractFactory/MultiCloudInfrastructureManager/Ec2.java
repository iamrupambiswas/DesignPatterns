package Creational.AbstractFactory.MultiCloudInfrastructureManager;

public class Ec2 implements ComputerInstance {

    @Override
    public void getInstance() {
        System.out.println("You got an AWS EC2 instance!");
    }
    
}
