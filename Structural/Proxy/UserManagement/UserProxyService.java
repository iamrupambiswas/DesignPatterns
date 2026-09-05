package Structural.Proxy.UserManagement;

public class UserProxyService implements UserService {
    private UserService userRealService;
    private String currentUser;

    public UserProxyService(UserService userService, String currentUser) {
        this.userRealService = userService;
        this.currentUser = currentUser;
    }

    @Override
    public void addUser(String user) {
        if (hasAccess(currentUser)) {
            userRealService.addUser(user);
        } else {
            System.out.println("Access denied for adding user: " + currentUser);
        }
    }

    @Override
    public void removeUser(String user) {
        if (hasAccess(currentUser)) {
            userRealService.removeUser(user);
        } else {
            System.out.println("Access denied for removing user: " + currentUser);
        }
    }

    @Override
    public boolean hasAccess(String user) {
        return userRealService.hasAccess(user);
    }
    
}
