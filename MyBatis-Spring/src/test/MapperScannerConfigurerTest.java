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
@MapperScan(basePackages = "mappers")
public class MapperScannerConfigurerTest {

    @Autowired
    private RoleMapper roleMapperScanner;

    @Test
    public void demo01(){
        roleMapperScanner.deleteRole(30);
    }
}
