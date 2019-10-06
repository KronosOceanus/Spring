package config;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration  //配置类
@ComponentScan(basePackages = {"mappers", "pojo", "service"}) //context:component-scan
@EnableTransactionManagement    //相当于 tx:annotation-driven
public class RootConfig implements TransactionManagementConfigurer { //该接口相当于 mvc:annotation-driven

    private DataSource dataSource = null;

    @Bean(name = "dataSource")
    public DataSource getDataSource(){
        //单例
        if (dataSource != null){
            return dataSource;
        }
        Properties props = new Properties();
        props.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        props.setProperty("url", "jdbc:mysql://127.0.0.1:3306/mybatis_intro?useSSL=no&serverTimezone=UTC");
        props.setProperty("username", "root");
        props.setProperty("password", "java521....");
        try{
                                                //该方法中可以看到数据源属性名
            dataSource = BasicDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean(name = "sqlSessionFactoryBean")
    public SqlSessionFactoryBean initSqlSessionFactory(){
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(getDataSource());
        Resource resource = new ClassPathResource("mybatis-config.xml");
        //不用理会异常
        sqlSessionFactory.setConfigLocation(resource);
        return sqlSessionFactory;
    }

    @Bean
    public MapperScannerConfigurer initMapperScannerConfigurer(){
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        msc.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        msc.setBasePackage("mappers");
        msc.setAnnotationClass(Repository.class);
        return msc;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(getDataSource());
        return transactionManager;
    }
}
