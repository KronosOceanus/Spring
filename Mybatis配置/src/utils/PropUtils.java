package utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Properties;

public class PropUtils {

    public static SqlSessionFactory getSqlSessionFactoryByAttributeCover(){
        String resource = "mybatis-config.xml";
        SqlSessionFactory sqlSessionFactory = null;
        try(
                InputStream inputStream = Resources.getResourceAsStream(resource);
                InputStream in = Resources.getResourceAsStream("jdbc.properties");
        ){
            Properties props = new Properties();
            props.load(in);
            String username = props.getProperty("database.username");
            String password = props.getProperty("database.password");
            //解压用户名和密码，放入属性中
            props.put("database.username",CodeUtils.decode(username));
            props.put("database.password",CodeUtils.decode(password));
            //覆盖原先 properties 参数
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream,props);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sqlSessionFactory;
    }
}
