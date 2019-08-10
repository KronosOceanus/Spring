[TOC]

# MyBatis-Spring 项目

## 配置步骤
* 导入 MyBatis-Spring.jar
* 配置数据源
* 配置 SqlSessionFactory
* 可选配置：SqlSessionTemplate（同时配置的时候，优先 SqlSessionFactory）
* 配置 Mapper
* 事务管理

## 配置 SqlSessionFactory
提供 SqlSessionFactoryBean 类支持配置 SqlSessionFactory
### SqlSessionFactoryBean 类源码简介
```java
    //实现 FactoryBean 接口，可以通过 getBean 方法获取对应的特殊 bean
    public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ApplicationEvent> {
        //日志
        private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryBean.class);
        // MyBatis 配置文件
        private Resource configLocation;
        // Configuration 对象
        private Configuration configuration;
        // Mapper 配置文件
        private Resource[] mapperLocations;
        //数据源
        private DataSource dataSource;
        //事务管理器
        private TransactionFactory transactionFactory;
        // 配置属性 
        private Properties configurationProperties;
        // Builder
        private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        //目标工厂
        private SqlSessionFactory sqlSessionFactory;
        //环境名
        private String environment = SqlSessionFactoryBean.class.getSimpleName();
        //加载后，是否检测所有的 MyBatis 映射语句加载完全，默认 false
        private boolean failFast;
        //插件
        private Interceptor[] plugins;
        //类型转换器
        private TypeHandler<?>[] typeHandlers;
        //类型转换器包名，扫描装载
        private String typeHandlersPackage;
        //别名
        private Class<?>[] typeAliases;
        //别名包名，扫描装载
        private String typeAliasesPackage;
        private Class<?> typeAliasesSuperType;
        //语言驱动
        private LanguageDriver[] scriptingLanguageDrivers;
        private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;
        //数据库厂商
        private DatabaseIdProvider databaseIdProvider;
        private Class<? extends VFS> vfs;
        //缓存
        private Cache cache;
        //对象工厂（创建结果集）
        private ObjectFactory objectFactory;
        //对象包装器
        private ObjectWrapperFactory objectWrapperFactory;
        
        //重点！
        //调用 SqlSessionFactoryBean 实际上返回的是 SqlSessionFactory
        public SqlSessionFactory getObject();
        
        /******* setter or other methods ********/
    }
    
```
可以配置 MyBatis 的所有组件，并且提供 setter 方法让 Spring（IoC） 去设置它们

### XML 配置
在 spring-config.xml 中
```xml
    <bean id="dataSource" class="org.springframework.jdbc.datasource.SimpleDriverDataSource">
        <!-- 注意！：该属性为驱动类 -->
        <property name="driverClass" value="com.mysql.cj.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://127.0.0.1:3306/mybatis_intro
                                      ?useSSL=false&amp;serverTimezone=UTC" />
        <property name="username" value="root" />
        <property name="password" value="java521...." />
    </bean>

    <!-- 该类实际返回是 SqlSessionFactory（通过 getObject 方法）-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource" />
        <!-- Spring 引入 MyBatis 配置文件 -->
        <property name="configLocation" value="classpath:mybatis-config.xml" />
    </bean>
```

## 配置 SqlSessionTemplate（可选/优先级 > SqlSessionFactory）
该类线程安全，每个线程的 SqlSession 唯一且互不冲突，有简单的增删查改功能
* XML 配置
```xml
    <!-- 线程安全类,每个线程的 SqlSession 唯一且互不冲突，可以完成简单增删查改 -->
    <bean id="sqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
        <!-- 配置工厂（报错不用理会） -->
        <constructor-arg ref="sqlSessionFactory" />
        <!-- 配置底层执行器 <constructor-arg value="BATCH" /> -->
    </bean>
```
* 测试：通过 Spring 容器得到 SqlSessionTemplate，然后
```java
                                //标志字符串，选定 SQL 语句   参数
    sqlSessionTemplate.update("mappers.RoleMapper.deleteRole", 1);
```
* 缺点：使用标志字符串，不符合面向对象思想，最好能够获取 Mapper 的实现类

## 配置 MapperFactoryBean
在 Spring 中获取单个 Mapper 实现类
* XML 配置
```xml
    <!-- 在 Spring 中获取单个 Mapper 实现类 -->
    <bean id="roleMapper" class="org.mybatis.spring.mapper.MapperFactoryBean">
        <!-- 映射器接口 -->
        <property name="mapperInterface" value="mappers.RoleMapper" />
        <!-- SqlSessionFactory 或者 SqlSessionTemplate -->
        <property name="sqlSessionFactory" ref="sqlSessionFactory" />
    </bean>
```
* 测试：通过 Spring 容器获得 RoleMapper 实现类
```java
    RoleMapper roleMapper = applicationContext.getBean("roleMapper", RoleMapper.class);
```
* 缺点：单个配置过于复杂，需要扫描配置

## 配置 MapperScannerConfigurer
扫描配置 Mapper
* XML 配置
```xml
    <!-- 配置 Mapper 扫描器 -->
    <bean id="roleMapperScanner" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="mappers" />
        <!-- 二选一 -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
        <!-- 指定要扫描类的注解（扫描带有 @Repository 注解的接口为 Mapper） -->
        <property name="annotationClass" value="org.springframework.stereotype.Repository" />
        <!-- 标记接口，继承了 BaseMapper 接口的接口作为 Mapper
            <property name="markerInterface" value="mappers.BaseMapper" /> -->
    </bean>
```
* 测试：直接创建扫描器，扫描器自动寻找对应 Mapper 并创建实现类
```java
    RoleMapper roleMapperScanner = applicationContext.getBean("roleMapperScanner", RoleMapper.class);
```
#### 注意！
同时配置 MapperFactoryBean 和 MapperScannerConfigurer，优先 MapperFactoryBean，且 MapperScanner
Configurer 会跳过 MapperFactoryBean 配置的 Mapper
