package config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

//注解配置初始化 Spring IoC 容器            抽象注解配置----初始化器
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    //Spring IoC 容器配置，用于装载各类 bean
    @Override
    protected Class<?>[] getRootConfigClasses() {
        //返回 Spring 的 Java 配置文件数组
        return new Class<?>[]{};
    }
    // DispatcherServlet 的 URI 映射关系配置，用于生成 web 请求上下文
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }
    //DispatcherServlet 的拦截内容（url-pattern）
    @Override
    protected String[] getServletMappings() {
        return new String[]{"*.do"};
    }
}
