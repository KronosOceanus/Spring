import annotation.AnnoConfig;
import annotation.StudentAction;
import collection.CollData;
import condition.ConConfig;
import condition.Human;
import dao_service.UserService;
import dao_service.UserServiceImpl;
import life_cycle.Maker;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import entity.Person;
import properties.ProConfig;
import properties.ProConfig2;

public class Test {

    private String xmlPath_1 = "entity/beans.xml";

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
        String xmlPath = "factory/static-factory.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath);
        //自动类型强转
        UserService userService = applicationContext.getBean(
                "userService", UserService.class);
        userService.addUser();
    }
    //实例工厂测试
    private void test04(){
        String xmlPath = "factory/class-factory.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath);
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
    //初始化前后方法测试
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
    //构造器/ setter属性注入测试
    private void test07(){
        String xmlPath = "di/constructor-properties.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        Person p = applicationContext.getBean("person",Person.class);

        System.out.println(p);
    }
    // p 命名空间测试
    private void test08(){
        String xmlPath = "di/p-namespace.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        Person p = applicationContext.getBean("person",Person.class);

        System.out.println(p);
    }
    //集合注入测试
    private void test09(){
        String xmlPath = "collection/setter-collection.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        CollData collData = applicationContext.getBean("collData",CollData.class);
        System.out.println(collData);
    }
    //注解 XML扫描 测试
    private void test10(){
        String xmlPath = "annotation/annotations.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        StudentAction studentAction = applicationContext.getBean("studentAction", StudentAction.class);
        studentAction.execute();
        System.out.println(studentAction.getName());
        studentAction.getUser().doSomething();
        studentAction.getStudentService().addStudent();
    }
    //生命周期测试
    private void test11(){
        String xmlPath = "life_cycle/life-cycle.xml";
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        Maker maker = applicationContext.getBean("maker",Maker.class);
        maker.make();
        //销毁 bean
        applicationContext.close();
    }
    //注解实现类扫描测试
    public void test12(){
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(AnnoConfig.class);
        StudentAction studentAction = applicationContext.getBean("studentAction", StudentAction.class);
        studentAction.execute();
        System.out.println(studentAction.getName());
        studentAction.getUser().doSomething();
        studentAction.getStudentService().addStudent();
    }
    //加载属性文件测试
    private void test13(){
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(ProConfig.class);
        String url = applicationContext.getEnvironment().getProperty("jdbc.url");
        System.out.println(url);

        applicationContext.register(ProConfig2.class);
        String allConfig = applicationContext.getBean("allConfig",String.class);
        System.out.println(allConfig);
    }
    // XML 加载属性文件测试
    private void test14(){
        String xmlPath = "properties/xml-config.xml";
        String xmlPath2 = "properties/xml-config2.xml";
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        String allConfig = applicationContext.getBean("allConfig",String.class);
        System.out.println(allConfig);
        System.out.println("=====================================");
        applicationContext.setConfigLocation(xmlPath2);
        allConfig = applicationContext.getBean("allConfig",String.class);
        System.out.println(allConfig);
    }
    //条件化 bean 测试
    private void test15(){
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(ConConfig.class);
        String humanName = applicationContext.getBean("humanName",String.class);
        System.out.println(humanName);
    }

    public static void main(String[] args) throws Exception {
        Test t = new Test();
        t.test11();
    }
}
