package aspectj_anno;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"aspectj_anno","entity"})
@EnableAspectJAutoProxy  //启动 AspectJ 自动代理
public class AspectJConfig {
}
