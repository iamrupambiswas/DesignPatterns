package Structural.Proxy.UserManagement;

public interface UserService {
    void addUser(String user);

    void removeUser(String user);

    boolean hasAccess(String user);
}
