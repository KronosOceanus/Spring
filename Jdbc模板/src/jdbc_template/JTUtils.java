package jdbc_template;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Service
public class JTUtils {

    private static BasicDataSource bds;
    public static JdbcTemplate jdbcTemplate;

    static{
        try {
            Properties props = new Properties();
            InputStream is = JTUtils.class.getClassLoader().
                    getResourceAsStream("db.properties");
            props.load(is);
            bds = BasicDataSourceFactory.createDataSource(props);
            jdbcTemplate = new JdbcTemplate(bds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //得到模板（封装成 bean）
    @Bean("jdbcTemplate")
    public static JdbcTemplate getJdbcTemplate(){
        if (jdbcTemplate == null){
            return new JdbcTemplate(bds);
        }else {
            return jdbcTemplate;
        }
    }

    //得到连接
    public static Connection getConnection(){
        try{
            return bds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
