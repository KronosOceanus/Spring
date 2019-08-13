import full_automatic_anno.FullAutomaticAnnoConfig;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import entity.AccountService;


public class Test {

    //数据源测试
    private void test01(){
        String xmlPath = "transfer/spring-config.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        BasicDataSource basicDataSource = applicationContext.getBean("dataSource",BasicDataSource.class);
        System.out.println(basicDataSource);
    }
    //转账测试
    private void test02(){
        String xmlPath = "transfer/spring-config.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        AccountService accountService = applicationContext.getBean("accountService",AccountService.class);
        accountService.transfer("jack", "rose", 1000);
    }
    //手动代理（TransactionTemplate）测试 ？？
    private void test03(){
        String xmlPath = "manual/manual.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        AccountService accountService = applicationContext.getBean(
                "accountService", AccountService.class);
        accountService.transfer("rose", "jack", 1000);
    }
    //半自动（TransactionProxyFactoryBean）代理测试
    private void test04(){
        String xmlPath = "semi_automatic/semi-automatic.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        AccountService accountService = applicationContext.getBean(
                "proxyAccountService", AccountService.class);
        accountService.transfer("rose", "jack", 1000);
    }
    // XML（AOP） 全自动代理测试
    private void test05(){
        String xmlPath = "full_automatic_xml/full-automatic-xml.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        AccountService accountService = applicationContext.getBean(
                "accountService", AccountService.class);
        accountService.transfer("jack", "rose", 1000);
    }
    //注解（XML）全自动代理测试
    private void test06(){
        String xmlPath = "full_automatic_anno/full-automatic-anno.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        AccountService accountService = applicationContext.getBean(
                "accountService", AccountService.class);
        accountService.transfer("rose", "jack", 1000);
    }
    //注解（Config.class）全自动代理测试 2
    private void test07(){
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(FullAutomaticAnnoConfig.class);
        AccountService accountService = applicationContext.getBean(
                "accountService", AccountService.class);
        accountService.transfer("rose", "jack", 1000);
    }
    // XML （TransactionInterceptor）全自动代理测试 ？？
    private void test08(){
        String xmlPath = "full_automatic_xml2/full-automatic-xml2.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        AccountService accountService = applicationContext.getBean("accountService", AccountService.class);
        accountService.transfer("jack","rose", 1000);
    }

    public static void main(String[] args) {
        Test t = new Test();
        t.test03();
    }
}
