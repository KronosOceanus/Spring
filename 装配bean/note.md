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
#### 执行 bean 的初始化和销毁方法
必须是单例的/使用 scope="prototype" 变成多例
```xml
    <bean id="userService" class="dao_service.UserServiceImpl" 
        init-method="init" destroy-method="destory">
```
#### BeanPostProcessor 后处理 bean
* Spring 工厂提供一个机制，只要实现此接口，并将实现类提供给 Spring 容器，Spring 
    容器将自动执行，初始化前执行 before，…… after
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
#### @Component("id")
```xml
    <bean id="id" class="">
```
### web 开发的三个衍生注解（与 @Component 作用一样）
##### @Repository：
dao 层
##### @Service：
service 层
##### @Controller：
web 层

#### @Value("v")
普通值注入
#### @Autowired
引用值，按照类型注入
#### @Autowired @Qualifier("name") / @Resource("name")
按照名称注入

#### @PostConstruct
初始化
#### @PreDestory
销毁

#### @Scope("")
* prototype：多例（默认是单例）



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






