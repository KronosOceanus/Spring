package entity;

public class UserServiceImpl implements UserService {

    @Override
    public void addUser() {
        System.out.println("jdk_proxy_addUser!");
    }

    @Override
    public void updateUser() {
        System.out.println("jdk_proxy_updateUser!");
    }

    @Override
    public void deleteUser() {
        System.out.println("jdk_proxy_deleteUser!");
    }
}
