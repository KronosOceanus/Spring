package entity;

import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Override
    public void addUser() {
        System.out.println("jdk_proxy_addUser!");
    }

    @Override
    public String updateUser() {
        System.out.println("jdk_proxy_updateUser!");
        int x = 1/0;
        return "蔡嵘！";
    }

    @Override
    public void deleteUser() {
        System.out.println("jdk_proxy_deleteUser!");
    }
}
