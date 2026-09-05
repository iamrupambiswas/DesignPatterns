package Structural.Proxy.UserManagement;

public class Main {
    public static void main(String[] args) {
        UserService userRealService = new UserRealService();
        String currentUser = "user"; // Change this to test access control
        UserService userProxyService = new UserProxyService(userRealService, currentUser);
        userProxyService.addUser("new_user");
        userProxyService.removeUser("new_user");
    }
}
