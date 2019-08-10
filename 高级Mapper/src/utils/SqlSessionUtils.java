package utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SqlSessionUtils {

    private final static Class<SqlSessionUtils> LOCK = SqlSessionUtils.class;
    private static SqlSessionFactory sqlSessionFactory = null;

    public static SqlSessionFactory getSqlSessionFactory(){
        synchronized (LOCK){
            if (sqlSessionFactory != null){
                return sqlSessionFactory;
            }
            String config = "mybatis-config.xml";
            try(
                    InputStream is = Resources.getResourceAsStream(config);
            ) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sqlSessionFactory;
        }
    }
    public static SqlSession openSqlSession(){
        if (sqlSessionFactory == null){
            getSqlSessionFactory();
        }
        return sqlSessionFactory.openSession();
    }
}
