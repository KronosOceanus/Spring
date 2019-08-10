import entity.User;
import dao.UserDao;
import jdbc_template.JDBCConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Test {

    // DBCP 测试
    private void test01(){
        User user = new User();
        user.setId(3);
        user.setUsername("csss");
        user.setPassword("8910");

        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(JDBCConfig.class);
        UserDao userDao = applicationContext.getBean("userDao",UserDao.class);
        userDao.update(user);

        List<User> users = userDao.findAll();
        for (User u : users){
            System.out.print(u.getUsername() + "\t");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Test t = new Test();
        t.test01();
    }
}
