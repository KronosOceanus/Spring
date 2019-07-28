import aspectj_anno.AspectJConfig;
import cglib_proxy.CGLIBBeanFactory;
import entity.User;
import jdk_proxy.JDKBeanFactory;
import entity.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import verifier.UserVerifier;
import verifier.VerifierConfig;

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
        User User = CGLIBBeanFactory.createUser();
        User.addUser();
        User.updateUser();
        User.deleteUser();
    }
    //半自动代理测试
    private void test03(){
        String xmlPath = "factory_bean_proxy/factory-bean-proxy.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        //获取代理 bean
        UserService userService = applicationContext.getBean("userServiceProxy", UserService.class);
        userService.addUser();
        userService.updateUser();
        userService.deleteUser();
    }
    //全自动代理测试
    private void test04(){
        String xmlPath = "auto_proxy/auto-proxy.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        //获取真实对象（但是 Spring 自动返回代理）
        UserService userService = applicationContext.getBean("userService", UserService.class);
        userService.addUser();
        userService.updateUser();
        userService.deleteUser();
    }
    // AspectJ XML 测试
    private void test05(){
        String xmlPath = "aspectj_xml/aspectj-xml.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        UserService userService = applicationContext.getBean("userService",UserService.class);
        userService.addUser();
        try{
            userService.updateUser();
        }catch (Exception e){

        }
        userService.deleteUser();
    }
    // Aspectj 注解测试
    private void test06(){
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(AspectJConfig.class);
        UserService userService = applicationContext.getBean("userService",UserService.class);
        userService.addUser();
        userService.updateUser();
        userService.deleteUser();
    }
    //引入测试
    private void test07(){
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(VerifierConfig.class);
        UserService userService = applicationContext.getBean("userService",UserService.class);
        UserVerifier userVerifier = (UserVerifier) userService;
        if (userVerifier.isNotNull(userService)){
            userService.addUser();
        }
    }
    //多个切面测试（责任链模式）
    private void test08(){
        String xmlPath = "multiple_aspects/multiple-aspects.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        UserService userService = applicationContext.getBean("userService",UserService.class);
        userService.addUser();
    }

    public static void main(String[] args) {
        Test t = new Test();
        t.test08();
    }
}
