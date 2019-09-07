package test;

import mappers.RoleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config.xml")
@MapperScan(basePackages = "mappers") //该注解与 Spring IoC 配置扫描 Mapper 相同
public class MapperScannerConfigurerTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    public void demo01(){
        roleMapper.deleteRole(41);
    }
}
