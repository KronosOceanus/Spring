package test;

import config.RedisConfig;
import config.RootConfig;
import mappers.RoleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pojo.Role;
import service.RoleService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, RedisConfig.class})
public class Test01 {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleService roleService;

    @Test
    public void test01(){
        System.out.println(roleMapper.getRole(1));
    }

    @Test
    public void test02(){
        System.out.println(roleService.getRole(1));
        Role role = new Role();
        role.setId(1);
        role.setRoleName("redis");
        role.setNote("redissssss");
        System.out.println(roleService.updateRole(role));
    }
}
