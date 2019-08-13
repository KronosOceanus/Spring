package full_automatic_anno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

@Configuration //配置
@ComponentScan("entity")
@EnableTransactionManagement //启用事务驱动管理器
@ImportResource("classpath:full_automatic_anno/full-automatic-anno.xml") //引入 XML 文件配置（数据源）
public class FullAutomaticAnnoConfig implements TransactionManagementConfigurer {
                                                //事务管理器配置接口
    //配置数据源 bean
    @Autowired
    private DataSource dataSource;
    //使用数据源 bean 配置 jdbcTemplate

    //实现接口方法，使用数据源 bean，配置平台事务管理器
    @Override   //相当于 XML 中的 <tx annotation-driven />
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        //实现类 数据源事务管理器
        DataSourceTransactionManager transactionManager =
                new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
