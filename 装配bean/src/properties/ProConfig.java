package properties;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

//读取配置文件，找不到就忽略（配置类）
@ComponentScan
@PropertySource(value = {"classpath:jdbc.properties"},
    ignoreResourceNotFound = true)
public class ProConfig {
}
