package profile;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Properties;

@Component
public class ProfileDataSource {

    //实际所用数据源
    @Bean(name = "devDataSource")
    @Profile("dev")
    public DataSource getDevDataSource(){
        Properties props = new Properties();
        props.setProperty("key","dev");

        DataSource dataSource = null;
        try{
            dataSource = BasicDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    //测试所用数据源
    @Bean(name = "testDataSource")
    @Profile("test")
    public DataSource getTestDataSource(){
        Properties props = new Properties();
        props.setProperty("key","test");

        DataSource dataSource = null;
        try{
            dataSource = BasicDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}
