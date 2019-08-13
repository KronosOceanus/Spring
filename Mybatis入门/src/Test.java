import entity.Role;
import mappers.RoleMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import utils.SqlSessionFactoryUtils;

import java.io.IOException;
import java.io.InputStream;

public class Test {

    //通过 XML 创建 SqlSessionFactory
    private SqlSessionFactory createSqlSessionFactoryByXml(){
        SqlSessionFactory sqlSessionFactory = null;
        String resource = "mybatis-config.xml";
        InputStream inputStream;
        try{
            //得到输入流
            inputStream = Resources.getResourceAsStream(resource);
            //利用 build 方法读取输入流创建 SqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sqlSessionFactory;
    }
    //通过代码创建 SqlSessionFactory
    private SqlSessionFactory createSqlSessionFactoryByCode(){
        //设置数据源
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mybatis_intro" +
                "?useSSL=false&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("java521....");
        dataSource.setDefaultAutoCommit(false);

        //设置事务管理器（事务工厂）和环境
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development",transactionFactory,dataSource);
        //创建配置对象（根标签）
        Configuration configuration = new Configuration(environment);
        //得到别名，添加映射器
        configuration.getTypeAliasRegistry().registerAlias("role", Role.class);
        configuration.addMapper(RoleMapper.class);
        //得到目标
        return new SqlSessionFactoryBuilder().build(configuration);
    }
    //SqlSession 测试
    private void test01(){
        try(
            SqlSession sqlSession = createSqlSessionFactoryByXml().openSession();
        ){
            Role role = sqlSession.selectOne("getRole",1);
            System.out.println(role);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //实例测试
    private void test02(){
        try(
                SqlSession sqlSession = SqlSessionFactoryUtils.openSqlSession();
                ){
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
            Role jack = new Role(null,"jack","is jack note!");
            Role rose = new Role(1,"rose","is rose note!");
            System.out.print(roleMapper.insertRole(jack) + "\t");
            System.out.println(roleMapper.updateRole(rose));
            for (Role role : roleMapper.findRoles("jack")){
                System.out.println(role);
            }
            sqlSession.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Test t = new Test();
        t.test02();
    }
}
