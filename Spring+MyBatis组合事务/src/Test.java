import entity.Role;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.RoleListService;

import java.util.ArrayList;
import java.util.List;

public class Test {

    // Spring + MyBatis 组合事务测试
    private void test01(){
        String xmlPath = "spring-config.xml";
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(xmlPath);
        RoleListService roleListService = applicationContext.getBean("roleListService", RoleListService.class);
        List<Role> roleList = new ArrayList<>();
        for(int i=0;i<2;i++){
            Role role = new Role(null, "role_name_" + i, "note_" + i);
            roleList.add(role);
        }
        int count = roleListService.insertRoleList(roleList);
        System.out.println(count);
    }
    public static void main(String[] args) {
        Test t = new Test();
        t.test01();
    }
}
