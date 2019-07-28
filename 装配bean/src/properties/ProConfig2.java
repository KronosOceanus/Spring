package properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@ComponentScan(basePackages = "properties")
@PropertySource(value = "classpath:jdbc.properties",
    ignoreResourceNotFound = true)
public class ProConfig2 {


    //该包下的配置文件直接与类的成员变量关联（在属性类中使用占位符）
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }
}
