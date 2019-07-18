import cglib_proxy.CGLIBBeanFactory;
import entity.User;
import jdk_proxy.JDKBeanFactory;
import entity.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    // jdk 动态代理测试
    private void test01(){
        UserService userService = JDKBeanFactory.createService();
        userService.addUser();
        userService.updateUser();
        userService.deleteUser();
    }
    // cglib 动态代理测试
    private void test02(){
        User user = CGLIBBeanFactory.createUser();
        user.addUser();
        user.updateUser();
        user.deleteUser();
    }
    //半自动代理测试
    private void test03(){
        String xmlPath = "factory_bean_proxy/factory-bean-proxy.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);

        UserService userService = applicationContext.getBean("userServiceProxy", UserService.class);
        userService.addUser();
        userService.updateUser();
        userService.deleteUser();
    }



    public static void main(String[] args) {
        Test t = new Test();
        t.test03();
    }
}
