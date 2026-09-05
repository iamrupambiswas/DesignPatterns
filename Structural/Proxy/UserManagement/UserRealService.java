package Structural.Proxy.UserManagement;

public class UserRealService implements UserService {
    @Override
    public void addUser(String user) {
        System.out.println("User added: " + user);
    }

    @Override
    public void removeUser(String user) {
        System.out.println("User removed: " + user);
    }

    @Override
    public boolean hasAccess(String user) {
        // Implement access control logic here
        return "admin".equals(user); // Example: only admin has access
    }
    
}
