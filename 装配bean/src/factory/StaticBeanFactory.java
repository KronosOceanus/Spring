package factory;

import dao_service.UserService;
import dao_service.UserServiceImpl;

/**
 * 静态工厂，全是 static 方法
 */
public class StaticBeanFactory {

    public static UserService createService(){
        return new UserServiceImpl();
    }
}
