import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:transfer/spring-config.xml")
public class DataSourceTest {

    @Autowired
    private BasicDataSource dataSource;

    @Test
    public void demo01(){
        System.out.println(dataSource);
    }
}
