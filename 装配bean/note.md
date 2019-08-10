[TOC]
# 学习内容
## IOC 控制反转
对象由Spring创建，即创建对象的权限由自己反转给Spring，需要对象得时候，从Spring的工厂
    （容器）中获得，需要将实现类的全名配置到 XML 文件中
例：
```xml
    <!-- 配置需要创建的对象，id用于用于从Spring中获取对象 -->
    <bean id="userDao" class="dao_service.UserDaoImpl" />
```
然后通过以下方式创建对象
```java
    //获取容器
    String xmlPath = "spring-config.xml";
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath);
    //获取内容
    UserService userService = (UserServiceImpl) applicationContext.getBean("userService");
```
## DI 依赖注入
自动执行某个对象的 entity 方法，
例：自动执行新建 userService 的 setUserDao 方法
```xml
    <bean id="userService" class="dao_service.UserServiceImpl">
        entity
        <property name="userDao" ref="userDao" />
    </bean>
```

## 核心API
#### BeanFactory（容器）
一个工厂，用于生产任意 bean，***getBean 的时候对象实例化***
#### ApplicationContext
BeanFactory的子接口,***当配置文件被加载，对象就已经实例化***，作用：
* 国际化处理
* 事件传递
* Bean 自动装配
* 各种不同应用层的 Context实现
#### ClassPathXmlApplicationContext
用于加载 classpath（类路径，src）下指定的 xml -> /WEB-INF/classes/...xml
#### FieldSystemXmlApplicationContext
加载制定盘符下的 xml -> /WEB-INF/...xml，通过 ServletContext.getRealPath 获得绝对路径

### BeanFactory 创建 bean
获取 bean 的过程（三目）：
1. 是否单例？是否缓存：创建 bean
2. 是否缓存？缓存：创建 bean
```java
    String xmlPath = "spring-config.xml";
    //将配置件转换为 Resource
    Resource resource = new ClassPathResource(xmlPath);
    BeanFactory beanFactory = new DefaultListableBeanFactory();
    //配置文件注册到某个工厂
    BeanDefinitionReader bdr = 
        new XmlBeanDefinitionReader((BeanDefinitionRegistry)beanFactory);
    //加载配置文件
    bdr.loadBeanDefinitions(resource);
    
    UserService userService = (UserService) beanFactory.getBean("userService");
```

## bean 实例化的三种方法
注入方式还是原来一样
#### 以上都是默认构造
#### 静态工厂
用于生产实例对象，所有的方法都是 static 方法，常用于 Spring 整合其他框架
#### 实例工厂
非 static

## bean 的种类
* 普通 bean：以上都是普通 bean，Spring直接创建对象
* FactoryBean：特殊的 bean，具有工厂生产对象的能力，只能生产特定的对象，bean必须
    使用 FactoryBean接口，此接口提供 getObject 方法获取特定 bean，
    例：ProxyFactoryBean 用于生产代理
    
## bean 作用域
#### singleton
单例，默认值
#### prototype
多例：每执行一次 getBean 获取一个实例

## bean 生命周期
### 总过程
执行方法过程：接口.方法
1. BeanNameAware.setBeanName：   设置名称
2. BeanFactoryAware.setBeanFactory： 设置从属工厂
3. ApplicationContextAware.setApplicationContext：   设置从属上下文
4. BeanPostProcessor.postProcessBeforeInitialization：   初始化前
5. InitializingBean.afterPropertiesSet： 注入属性后
6. xml配置中.init： 自定义初始化
7. BeanPostProcessor.postProcessAfterInitialization：    初始化后
8.  真实对象的方法
9. DisposableBean.destory：  系统销毁
10. xml配置中.destory： 自定义销毁
#### 执行 bean 的初始化和销毁方法
必须是单例的/使用 scope="prototype" 变成多例
```xml
    <bean id="userService" class="dao_service.UserServiceImpl" 
        init-method="init" destroy-method="destory">
```
#### BeanPostProcessor 后处理 bean
* Spring 工厂提供一个机制，只要实现此接口，并将实现类提供给 Spring 容器，Spring 
    容器将自动执行，初始化前执行 before，初始化后 after
* Spring 提供工厂钩子，用于修改实例对象，可以生成代理对象，是 AOP 底层
    （钩子：这两方法需要传入一个 Object 对象，然后在方法中修改这个对象，再使用 jdk 
        动态代理返回代理，可以赋给原对象）
* 设置代理对象，在目标方法前后执行（例：开始/提交事务、性能监控）
* 对所有 bean 生效，如果只想对一个 bean 生效，则执行判断 xxx.equals(beanName)

## entity 属性注入
#### 构造方法（推荐使用）
* index：参数索引（0 开始）
* value：常量
* ref：引用
#### setter
* value：常量
* ref：引用

```xml
    <bean id="person" class="entity.Person" >
        <!-- 构造器注入 -->
        <constructor-arg index="0" value="李俊峡" />
        <constructor-arg index="1" ref="homeAddr" />

        <!-- setter 方法注入 -->
        <property name="age"><value>18</value></property>
        <property name="companyAddr"><ref bean="companyAddr" /></property>
    </bean>
```

#### p 命名空间（了解）
简化了 entity 属性注入，替换了 property 标签
```xml
    xmlns="http://www.springframework.org/schema/beans"
```
增加一条
```xml
    xmlns:p="http://www.springframework.org/schema/p"
```
使用方法
```xml
    <bean id="person" class="entity.Person"
          p:name="李俊峡" p:age="18" 
          p:homeAddr-ref="homeAddr" p:companyAddr-ref="companyAddr"/>
```

#### SpEL（了解）
井{SpEL}  相当于 EL 表达式，可以动态注入属性
* 常量
* 引用 bean /属性/方法
* 引用静态方法
* 支持 运算符/正则表达式/集合

#### 集合
在 property 中使用
* array
* list
* set
* map
* props
等标签完成集合属性注入
```xml
    <!-- array 完成属性注入  -->
    <property name="arrayData">
        <array>
            <!-- 普通数据 -->
            <value>DS</value>
        </array>
    </property>
    
    <!-- map 完成属性注入 -->
    <property name="mapData">
        <map>
            <entry key="jack" value="杰克" />
            <entry>
                <key><value>rose</value></key>
                <value>肉丝</value>
            </entry>
        </map>
    </property>
    
    <!-- props 属性注入 -->
    <property name="properties">
        <props>
            <prop key="高富帅">nao</prop>
            <prop key="白富美">miao</prop>
        </props>
    </property>
```

## 注解装配 bean
下面等价
###### @Component("id")
```xml
    <bean id="id" class="">
```
###### web 开发的三个衍生注解（与 @Component 作用一样）
* @Repository（仓库）：
dao 层
* @Service：
service 层
* @Controller：
web 层

###### @Value("v")
普通值注入
###### @Autowired
引用值，按照类型注入
###### @Autowired @Qualifier("name")
按照名称注入
###### @Autowired @Primary("name")
优先使用该类注入

###### @PostConstruct
初始化
###### @PreDestory
销毁

###### @Scope()
值为 ConfigurableBeanFactory 类的常量
* prototype：多例（默认是单例）
* session/request：会话/请求

#### @Bean
注解到方法之上，将返回值封装成一个 bean，有如下 4 个属性
* name：beanName
* autowire：该 bean 是否是引用对象
* init / destoryMethod：初始化/销毁方法

###### @Import
在一个配置类中导入其他（多个）配置类


## 扫描注解（两种方法）
#### XML 文件扫描
创建 ApplicaitonContext 时扫描该文件
```xml
<!-- 使用注解需要增加3条命名空间 -->
    <!--
        xmlns:context="http://www.springframework.org/schema/context"

        //写在 xsi:schemaLocation 中，成对存在
        http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context.xsd
    -->

    <!-- 组件扫描，扫描有注解的类       包名-->
    <context:component-scan base-package="annotation" />
```
#### 配置类扫描
创建 *AnnotationConfigApplicationContext*（ApplicationContext 的子类） 时传入该类（.class）
```java
    //扫描当前包下的 bean @ComponentScan
    //扫描指定包（多个）
    @ComponentScan(basePackages = {"annotation", "entity"})
    public class ApplicationConfig {
}
```

## 注解实现属性文件映射
#### XML 实现
属性类
```java
    /**
     * 将属性文件与成员变量建立映射
     */
    @Component
    public class DatabaseConfig {
    
        //属性 key
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
    }
```
XML 配置文件

```xml
    <context:comhumannt-scan base-package="properties" />

    <!-- 相当于 PropertySource 注解，使 Spring 增加映射功能 -->
    <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="locations">
            <array>
                <!-- 允许多个 value 配置多个属性文件 -->
                <value>classpath:jdbc.properties</value>
            </array>
        </property>
        <property name="ignoreResourceNotFound" value="true" />
    </bean>
```

测试
#### 配置类实现
映射（配置）类（使 Spring 增加映射功能）

```java
    @ComponentScan(basePackages = "properties")
    @PropertySource(value = "classpath:jdbc.properties",
        ignoreResourceNotFound = true)
    public class ProConfig2 {
    
    
        //该包下的配置文件直接与类的成员变量关联
        @Bean
        public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
```
## 条件化装配 bean
bean
```java
    //以 @Bean 注解定义的 bean（如下）
    @Bean("humanName")
    @Conditional(ConditionClass.class) //使用该注解指定判断条件类
    public String getName() {
        return name;
    }
```
判断条件类
```java
    /**
     * 根据条件装配 bean
     * 如果装配失败会抛出异常
     */
    public class ConditionClass implements Condition {
        @Override              //运行时环境                       //获得关于该 bean 的注解信息
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            /**
             * 如果 bean 是引用类型，可以通过以下方法获得 bean 的属性
             * Environment env = conditionContext.getEnvironment();
             * String prop = env.getProperty("propId");
             */
    
            //是否创建 bean
            return true;
        }
    }
```
然后创建 bean，则会判断条件再决定是否装配





# 总结
## 编写流程
1. 导入jar包（4 + 1）：beans/core/context/expression + commons-logging
2. 编写目标类（dao / service）
3. spring 配置文件
    * ioc
    * di
    * 实例化：默认构造，静态/实例工厂
    * 作用域：scope
    * 生命周期： init-method，destory-method
    * 后处理 bean：实现 BeanPostProcessor 接口的 bean
4. 属性注入
    * 构造方法注入
    * setter 方法注入
    * p 命名空间
    * SpEL
    * 集合注入
5. 核心 API……
6. 注解
    * 扫描
    * ioc
    * di






