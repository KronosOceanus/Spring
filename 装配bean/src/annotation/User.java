package annotation;

import org.springframework.stereotype.Component;

/**
 * 需要在对应 xml 配置文件中进行组件扫描
 * 扫描该包中哪些类包含注解
 */
@Component("user")
public class User {

    public void doSomething(){
        System.out.println("doSomething!");
    }
}
