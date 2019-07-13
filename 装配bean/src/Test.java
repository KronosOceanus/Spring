import annotation.StudentAction;
import collection.CollData;
import dao_service.UserService;
import dao_service.UserServiceImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import entity.Person;

public class Test {

    private String xmlPath_1 = "beans.xml";
    private String xmlPath_2 = "static-factory.xml";
    private String xmlPath_3 = "class-factory.xml";
    private String xmlPath_4 = "constructor-properties.xml";
    private String xmlPath_5 = "p-namespace.xml";
    private String xmlPath_6 = "setter-collection.xml";
    private String xmlPath_7 = "anno-beans.xml";


    //ioc 和 di 测试
    private void test01(){
        //获取容器（读取配置文件，bean实例化）
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath_1);
        //获取内容
        UserService userService = (UserServiceImpl) applicationContext.getBean("userService");
        userService.addUser();
    }
    //BeanFactory 测试
    private void test02(){
        //将配置件转换为 Resource
        Resource resource = new ClassPathResource(xmlPath_1);
        BeanFactory beanFactory = new DefaultListableBeanFactory();
        //配置文件注册到某个工厂
        BeanDefinitionReader bdr = new XmlBeanDefinitionReader((BeanDefinitionRegistry)beanFactory);
        //加载配置文件
        bdr.loadBeanDefinitions(resource);

        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.addUser();
    }
    //静态工厂测试
    private void test03(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath_2);
        //自动类型强转
        UserService userService = applicationContext.getBean(
                "userService", UserService.class);
        userService.addUser();
    }
    //实例工厂测试
    private void test04(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath_3);
        //自动类型强转
        UserService userService = applicationContext.getBean(
                "userService", UserService.class);
        userService.addUser();
    }
    //作用域测试（多例）
    private void test05(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath_1);
        UserService userService = applicationContext.getBean("userService", UserService.class);
        UserService userService2 = applicationContext.getBean("userService", UserService.class);

        userService.addUser();
        userService2.addUser();
    }
    // init / destory（容器关闭执行） 方法测试
    // 初始化前/销毁后方法测试
    private void test06() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath_1);
        UserService userService = applicationContext.getBean("userService", UserService.class);

        userService.addUser();

        /**
         * 反射关闭容器
         * 接口中没有 close 方法，但实现类中有
         * applicationContext.getClass().getMethod("close").invoke(applicationContext);
         */
        //直接使用子类，含有 close 方法
        applicationContext.close();
    }
    // 构造器/ setter属性注入测试
    private void test07(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath_4);
        Person p = applicationContext.getBean("person",Person.class);

        System.out.println(p);
    }
    // p 命名空间测试
    private void test08(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath_5);
        Person p = applicationContext.getBean("person",Person.class);

        System.out.println(p);
    }
    // 集合注入测试
    private void test09(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath_6);
        CollData collData = applicationContext.getBean("collData",CollData.class);
        System.out.println(collData);
    }
    // 注解测试
    private void test10(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath_7);
        StudentAction studentAction = applicationContext.getBean("studentAction", StudentAction.class);
        studentAction.execute();
        System.out.println(studentAction.getName());
        studentAction.getUser().doSomething();
        studentAction.getStudentService().addStudent();
    }


    public static void main(String[] args) throws Exception {
        Test t = new Test();
        /**
        t.test01();
        t.test02();
        t.test03();
        t.test04();
        t.test05();
        t.test06();
        t.test08();
        t.test09();
        t.test10();
         */
        t.test07();
    }
}
