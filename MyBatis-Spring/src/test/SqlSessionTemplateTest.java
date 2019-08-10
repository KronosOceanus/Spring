package test;

import entity.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config.xml")
public class SqlSessionTemplateTest {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Test
    public void demo01(){
        Role role = new Role(null, "java", "java note!");
        sqlSessionTemplate.insert("mappers.RoleMapper.insertRole", role);
    }
}
