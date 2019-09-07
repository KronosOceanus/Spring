import mappers.RoleMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    // MapperScannerConfigurer 测试
    private void test01(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("spring-config.xml");
        RoleMapper roleMapper = applicationContext.getBean(RoleMapper.class);
        System.out.println(roleMapper.getRole(15));
    }


    public static void main(String[] args) {
        Test t = new Test();
        t.test01();
    }
}
