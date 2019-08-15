[TOC]

# Spring MVC
## 简单实例
#### web.xml 配置
```xml
    <!-- Spring IoC 配置文件路径 -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/applicationContext.xml</param-value>
    </context-param>
    <!-- ContextLoaderListener（实现了 ServletContextListener） 用以初始化 Spring IoC 容器 -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- 核心 Servlet -->
    <servlet>
        <!-- MVC 框架会根据 servlet-name 加载文件（例：/WEB-INF/dispathcer-servlet.xml）配置 -->
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- 服务器启动初始化 -->
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <!-- 拦截后缀为 do 的请求 -->
        <url-pattern>*.do</url-pattern>
    </servlet-mapping>
```
#### dispatcher-servlet.xml 配置（配置文件名与 web.xml 中 servlet-name 有关）
引入 p, tx, context, mvc 等 4 个命名空间
```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
           xmlns:p="http://www.springframework.org/schema/p" xmlns:tx="http://www.springframework.org/schema/tx"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    
        <!-- Spring MVC 配置，初始化器 -->
    
        <!-- 注解驱动 MVC -->
        <mvc:annotation-driven />
        <!-- 扫描装载的包 -->
        <context:component-scan base-package="controller" />
    
        <!-- 定义视图解析器 -->
        <!-- 找到 Web 工程 /WEB-INF/JSP 文件夹，且文件结尾为 .jsp 的文件作为映射 -->
        <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver"
              p:prefix="/WEB-INF/jsp/" p:suffix=".jsp" />
    
        <!--
            如果需要开启数据库事务，需要开启这段代码
            <tx:annotation-driven transaction-manager="transactionManager" /> -->
    </beans>
```
#### 简单 Controller
```java
    //标明控制器
    @Controller("myController")
    //标明在当前请求的 URI 在 /my 下才有该控制器响应（包名）
    @RequestMapping("/my")
    public class MyController {
    
        //标明 URL 是 /index 的时候该方法才请求（页面名）
        //对应 URL（localhost:8080/my/index.do）
        @RequestMapping("/index")
        public ModelAndView index(){
            ModelAndView mv = new ModelAndView();
            //视图逻辑名称
            mv.setViewName("index");
            return mv;
        }
    }
```
#### 编写视图文件（/WEB-INF/jsp/index.jsp）
```jsp
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
      <head>
        <title>$Title$</title>
      </head>
      <body>
      <h1>
        Hello, Spring MVC
      </h1>
      </body>
    </html>
```

## 执行流程
1. 启动：解析 MyController 的注解，生成对应的 URI 和请求映射关系
2. 请求：根据 URI 找到对应 HandlerMapping，组织一个 HandlerExecutionChain，通过请求类型找到
    RequestMappingHandlerAdapter（在 DispatcherServlet 初始化时创建），通过它执行 
        HandlerExecutionChain 内容
3. 视图渲染：在 MyController 的方法中将 index 视图返回 DispatcherServlet，视图解析器寻找对应
    视图文件，作为视图
    
## 初始化 Spring IoC 上下文
通过 ServletContextListener 接口（ContextLoaderListener 实现类）完成初始化和销毁 Spring IoC 容器
#### ContextLoaderListener 源码
```java
    public class ContextLoaderListener extends ContextLoader implements ServletContextListener {
        public ContextLoaderListener() {
        }
    
        public ContextLoaderListener(WebApplicationContext context) {
            super(context);
        }
    
        // Spring IoC 初始化
        public void contextInitialized(ServletContextEvent event) {
            this.initWebApplicationContext(event.getServletContext());
        }
        //销毁
        public void contextDestroyed(ServletContextEvent event) {
            this.closeWebApplicationContext(event.getServletContext());
            ContextCleanupListener.cleanupAttributes(event.getServletContext());
        }
    }
```
## 初始化映射请求上下文
* 注意！：如果没有注册 ContextLoaderListener，则 Spring IoC 会在 DispatcherServlet 初始化的时候
    完成初始化，由于 DispatcherServlet 可能用到 Spring IoC 的资源，所以建议服务器启动直接初始化
### DispatcherServlet 初始化
父类依次是 FrameworkServlet，HttpServletBean， HttpServlet，所以可以当做一个普通 Servlet 看待，
初始化调用 init 方法，该方法在其父类 HttpServletBean 中
#### HttpServletBean 类 init 方法源码
```java
    public final void init() throws ServletException {
        //打印日志
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Initializing servlet '" + this.getServletName() + "'");
        }
        
        //根据参数初始化 bean 的属性
        PropertyValues pvs = new HttpServletBean.ServletConfigPropertyValues(this.getServletConfig(), this.requiredProperties);
        //参数为空时的行为
        if (!pvs.isEmpty()) {
            try {
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
                ResourceLoader resourceLoader = new ServletContextResourceLoader(this.getServletContext());
                bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, this.getEnvironment()));
                this.initBeanWrapper(bw);
                bw.setPropertyValues(pvs, true);
            } catch (BeansException var4) {
                if (this.logger.isErrorEnabled()) {
                    this.logger.error("Failed to set bean properties on servlet '" + this.getServletName() + "'", var4);
                }
                throw var4;
            }
        }
        
        //该方法交给子类实现（所以接下来看 FrameworkServlet 类 initServletBean 方法源码）
        this.initServletBean();
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Servlet '" + this.getServletName() + "' configured successfully");
        }
    }
```
#### FrameworkServlet 类 initServletBean 等方法源码
```java
    //初始化 DispatcherServlet
    protected final void initServletBean() throws ServletException {
        //日志
        this.getServletContext().log("Initializing Spring FrameworkServlet '" + this.getServletName() + "'");
        if (this.logger.isInfoEnabled()) {
            this.logger.info("FrameworkServlet '" + this.getServletName() + "': initialization started");
        }
        long startTime = System.currentTimeMillis();


        try {
            //初始化 Spring IoC 容器
            this.webApplicationContext = this.initWebApplicationContext();
            
            this.initFrameworkServlet();
        } catch (ServletException var5) {
            this.logger.error("Context initialization failed", var5);
            throw var5;
        } catch (RuntimeException var6) {
            this.logger.error("Context initialization failed", var6);
            throw var6;
        }

        //日志
        if (this.logger.isInfoEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            this.logger.info("FrameworkServlet '" + this.getServletName() + "': initialization completed in " + elapsedTime + " ms");
        }
    }
    
    //初始化 Spring IoC 容器
    protected WebApplicationContext initWebApplicationContext() {
            WebApplicationContext rootContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
            WebApplicationContext wac = null;
            
            //判断是否已经初始化
            if (this.webApplicationContext != null) {
                //如果已经在启动时候创建，直接获取
                wac = this.webApplicationContext;
                if (wac instanceof ConfigurableWebApplicationContext) {
                    ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext)wac;
                    //如果 Spring IoC 容器还没有刷新
                    if (!cwac.isActive()) {
                        //如果没有父容器，设置父容器
                        if (cwac.getParent() == null) {
                            cwac.setParent(rootContext);
                        }
                        //刷新父容器上下文
                        this.configureAndRefreshWebApplicationContext(cwac);
                    }
                }
            }
    
            //没有初始化，则查找是否有存在的 Spring IoC 容器
            if (wac == null) {
                wac = this.findWebApplicationContext();
            }
            //没有初始化也不存在，则由 DispatcherServlet 创建
            if (wac == null) {
                wac = this.createWebApplicationContext(rootContext);
            }
            //没有刷新过，刷新
            if (!this.refreshEventReceived) {
                
                //刷新！（在 DispatcherServlet 类中，重点方法，下面查看源码）
                this.onRefresh(wac);
            }
    
            if (this.publishContext) {
                String attrName = this.getServletContextAttributeName();
                
                //将 Spring Ioc 容器设置到 ServletContext（整个 web 应用） 中
                this.getServletContext().setAttribute(attrName, wac);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Published WebApplicationContext of servlet '" + this.getServletName() + "' as ServletContext attribute with name [" + attrName + "]");
                }
            }
            //得到容器
            return wac;
        }
```
#### DispatcherServlet 类的 onRefresh 方法源码
包含 Spring MVC 的核心组件
```java
    protected void onRefresh(ApplicationContext context) {
            this.initStrategies(context);
        }

    protected void initStrategies(ApplicationContext context) {
        //解析初始化文件
        this.initMultipartResolver(context);
        //本地解析化
        this.initLocaleResolver(context);
        //主题解析
        this.initThemeResolver(context);
        //处理器映射
        this.initHandlerMappings(context);
        //处理器适配器
        this.initHandlerAdapters(context);
        // Handler 的异常处理器
        this.initHandlerExceptionResolvers(context);
        //处理器没有返回逻辑视图名称，自动将 URL 映射为逻辑视图名
        this.initRequestToViewNameTranslator(context);
        //视图解析器
        this.initViewResolvers(context);
        this.initFlashMapManager(context);
    }
```
#### 核心组件介绍
* MultipartResolver：文件解析器，支持服务器文件上传
* LocaleResolver：国际化解析器，提供国际化功能
* ThemeResolver：主题解析器，类似于皮肤
* HandlerMapping：处理器映射，包含一系列拦截器和控制器方法
* HandlerAdapter：处理器适配器，因为处理器会在不同的上下文之间运行，所以 Spring MVC 先找到合适的
    适配器，用来运行处理器服务方法
* HandlerExceptionResolver：处理器异常解析器，比如出现异常后，跳转到指定的异常页面
* RequestToViewNameTranslator：视图逻辑名称转换器，如果 URI 没有返回逻辑视图名，则自动将请求的
    URI 映射为逻辑视图名
* ViewResolver：视图解析器，定位视图

这些组件 DispatcherServlet 会根据其配置文件 DispatcherServlet.properties 初始化

## 注解配置方式初始化
#### 替换 web.xml 文件（不能同时配置）
以类代替 web.xml 配置文件，只需要该类实现 AbstractAnnotationConfigDispatcherServletInitializer 接口，
例：
```java
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
```
#### 替换 dispatcher-servlet.xml 文件
```java
    //相当于 dispatcher-servlet.xml
    @Configuration //声明是配置类
    @ComponentScan("controller")
    @EnableWebMvc // MVC 注解驱动
    public class WebConfig {
    
        //视图解析器
        @Bean("viewResolver")
        public ViewResolver initViewResolver(){
            InternalResourceViewResolver viewResolver =
                    new InternalResourceViewResolver();
            viewResolver.setPrefix("/WEB-INF/jsp/");
            viewResolver.setSuffix(".jsp");
            return viewResolver;
        }
    }
```
### 原理
Servlet 3.0 之后允许动态加载 Servlet，要求实现 ServletContainerInitializer 接口（Servlet 内容初始化器）
，于是 MVC 写了一个 SpringServletContainerInitializer 实现了该接口
#### SpringServletContainerInitializer 源码
```java
    //定义初始化类型，只要实现 WebApplicationInitializer 接口则为初始化器
    @HandlesTypes({WebApplicationInitializer.class})
    public class SpringServletContainerInitializer implements ServletContainerInitializer {
    
        public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) throws ServletException {
            //多个初始化器
            List<WebApplicationInitializer> initializers = new LinkedList();
            Iterator var4;
            //从各个包中加载对应的初始化器
            if (webAppInitializerClasses != null) {
                var4 = webAppInitializerClasses.iterator();
    
                while(var4.hasNext()) {
                    Class<?> waiClass = (Class)var4.next();
                    if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) && WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
                        try {
                            initializers.add((WebApplicationInitializer)waiClass.newInstance());
                        } catch (Throwable var7) {
                            throw new ServletException("Failed to instantiate WebApplicationInitializer class", var7);
                        }
                    }
                }
            }
            
            //找不到初始化器
            if (initializers.isEmpty()) {
                servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
            } else {
                servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
                AnnotationAwareOrderComparator.sort(initializers);
                var4 = initializers.iterator();
    
                //调用各个初始化器
                while(var4.hasNext()) {
                    WebApplicationInitializer initializer = (WebApplicationInitializer)var4.next();
                    initializer.onStartup(servletContext);
                }
    
            }
        }
    }
```
一个类实现了 WebApplicationInitializer 接口的 onStartup 方法，就会被当做初始化器加载进来
* AbstractAnnotationConfigDispatcherServletInitializer 依次父类：
    AbstractDispatcherServletInitializer，AbstarctContextLoaderInitializer，
        WebApplicationInitializer，所以 MyWebAppInitializer 会被当做初始化器
