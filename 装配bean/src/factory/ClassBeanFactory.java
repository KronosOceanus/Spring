package factory;

import dao_service.UserDao;
import dao_service.UserService;
import dao_service.UserServiceImpl;

/**
 * 实例工厂，全是非 static 方法
 */
public class ClassBeanFactory {

    public UserService createService(){
        return new UserServiceImpl();
    }
}
