package properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 将属性文件与成员变量建立映射
 */
@Component
public class DatabaseConfig {

    @Value("${jdbc.driver}")
    private String driver = null;
    @Value("${jdbc.url}")
    private String url = null;
    @Value("${jdbc.user}")
    private String user = null;
    @Value("${jdbc.pass}")
    private String pass = null;

    @Bean(name = "allConfig")
    public String getAll(){
        return driver + "\n" + url + "\n" + user + "\n" + pass;
    }

















    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
