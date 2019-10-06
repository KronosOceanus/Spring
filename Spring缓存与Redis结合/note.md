[TOC]
# Spring 缓存与 Redis 结合
## Redis 和数据库结合
#### 数据更新策略
* 可以在某段时间刷新，一般用于不太重要的数据
* 实时更新，尤其是当前用户的交易记录，购买时商品的数量等
#### Redis 和数据库的读/写操作
* 读：Redis 数据超时后，就会触发程序读取数据库，然后将读取的数据写入 Redis
* 写：从数据库读取最新数据，然后进行操作，最后写入 Redis

## Spring 整合 Redis
#### 测试环境
###### pojo
```java
    //必须可序列化
    public class Role implements Serializable {
        //19 位置
        private static final long serialVersionUID = 1122334455667788995L;
    
        private int id;
        private String roleName;
        private String note;
        
        @Override
        public String toString() {
            return "id=" + id + ", roleName=" + roleName + ", note=" + note;
        }
    }
```
###### RoleMpper.xml
```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    
    <mapper namespace="mappers.RoleMapper">
        <sql id="pro">
            id, role_name as roleName, note
        </sql>
    
        <select id="getRole" resultType="role" parameterType="integer">
            select <include refid="pro"/> from t_role where id = #{id}
        </select>
        <!-- 根据属性模糊查询 -->
        <select id="findRoles" resultType="Role">
            select <include refid="pro"/> from t_role
            <where>
                <if test="roleName != null">
                    role_name like concat('%', #{roleName}, '%')
                </if>
                <if test="note != null">
                    note like concat('%', #{note}, '%')
                </if>
            </where>
        </select>
    
        <insert id="insertRole" parameterType="role"
            useGeneratedKeys="true" keyProperty="id">
            insert into t_role(role_name, note) values(#{roleName}, #{note})
        </insert>
    
        <update id="updateRole" parameterType="role">
            update t_role set role_name = #{roleName}, note = #{note}
              where id = #{id}
        </update>
    
        <delete id="deleteRole" parameterType="integer">
            delete from t_role where id = #{id}
        </delete>
    </mapper>
```
###### RoleMapper 接口
```java
    @Repository
    public interface RoleMapper {
    
        Role getRole(Integer id);
    
        int deleteRole(Integer id);
    
        int insertRole(Role role);
    
        int updateRole(Role role);
    
        List<Role> findRoles(@Param("roleName") String roleName,
                             @Param("note") String note);
    }
```
###### RoleService
```java
    public interface RoleService {
    
        Role getRole(Integer id);
    
        int deleteRole(Integer id);
    
        Role insertRole(Role role);
    
        Role updateRole(Role role);
    
        List<Role> findRoles(String roleName, String note);
    }
```
###### applicationContext.xml 配置（java 形式）
```java
    @Configuration  //配置类
    @ComponentScan(basePackages = {"mappers", "pojo", "service", "redis_service"}) //context:component-scan
    @EnableTransactionManagement    //相当于 tx:annotation-driven
    public class RootConfig implements TransactionManagementConfigurer { //该接口相当于 mvc:annotation-driven
    
        private DataSource dataSource = null;
    
        @Bean(name = "dataSource")
        public DataSource getDataSource(){
            //单例
            if (dataSource != null){
                return dataSource;
            }
            Properties props = new Properties();
            props.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
            props.setProperty("url", "jdbc:mysql://127.0.0.1:3306/mybatis_intro?useSSL=no&serverTimezone=UTC");
            props.setProperty("username", "root");
            props.setProperty("password", "java521....");
            try{
                                                    //该方法中可以看到数据源属性名
                dataSource = BasicDataSourceFactory.createDataSource(props);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dataSource;
        }
    
        @Bean(name = "sqlSessionFactoryBean")
        public SqlSessionFactoryBean initSqlSessionFactory(){
            SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
            sqlSessionFactory.setDataSource(getDataSource());
            Resource resource = new ClassPathResource("mybatis-config.xml");
            //不用理会异常
            sqlSessionFactory.setConfigLocation(resource);
            return sqlSessionFactory;
        }
    
        @Bean
        public MapperScannerConfigurer initMapperScannerConfigurer(){
            MapperScannerConfigurer msc = new MapperScannerConfigurer();
            msc.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
            msc.setBasePackage("mappers");
            msc.setAnnotationClass(Repository.class);
            return msc;
        }
    
        @Override
        public PlatformTransactionManager annotationDrivenTransactionManager() {
            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
            transactionManager.setDataSource(getDataSource());
            return transactionManager;
        }
    }
```
###### mybatis-config.xml
```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration
            PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-config.dtd">
    
    <configuration>
        <typeAliases>
            <typeAlias type="pojo.Role" alias="role" />
        </typeAliases>
    
    
        <mappers>
            <mapper resource="mappers/RoleMapper.xml" />
        </mappers>
    </configuration>
```
#### 配置缓存管理器
缓存管理器实现 CacheManager 接口，Redis 实现类为 RedisCacheManager
###### java 配置
```java
    // java 配置 Spring 缓存管理器
    @Configuration
    @EnableCaching //启用 Spring 缓存机制
    public class RedisConfig {
    
        @Bean("redisTemplate")
        public RedisTemplate initRedisTemplate(){
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxIdle(50);
            poolConfig.setMaxTotal(100);
            poolConfig.setMaxWaitMillis(20000);
    
            JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
            connectionFactory.setPoolConfig(poolConfig);
            connectionFactory.setHostName("localhost");
            connectionFactory.setPort(6379);
            connectionFactory.afterPropertiesSet(); //手动初始化 bean（如果是单独的 bean，Spring 会自动初始化）
    
            JdkSerializationRedisSerializer jdkSerializationRedisSerializer =
                    new JdkSerializationRedisSerializer();
            StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    
            RedisTemplate redisTemplate = new RedisTemplate();
            redisTemplate.setConnectionFactory(connectionFactory);
    
            redisTemplate.setDefaultSerializer(stringRedisSerializer);
            redisTemplate.setKeySerializer(stringRedisSerializer);
            redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);
            redisTemplate.setHashKeySerializer(stringRedisSerializer);
            redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer);
    
            return redisTemplate;
        }
    
        @Bean(name = "redisCacheManager")
        public RedisCacheManager initRedisCacheManager(@Autowired RedisTemplate redisTemplate){
            RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
            //设置超时时间，保证 java 在一定时间间隔后重新从数据读取数据
            redisCacheManager.setDefaultExpiration(600);
            //设置缓存名称
            List<String> cacheNames = new ArrayList<>();
            cacheNames.add("redisCacheManager");
            redisCacheManager.setCacheNames(cacheNames);
            return redisCacheManager;
        }
    }
```
* ***注意！：上面的 JedisConnectionFactory 需要自己调用 afterPropertiesSet 方法初始化（这个类实现了 
    InitializingBean 接口，单独定义则在 Spring 周期中会被 Spring IoC 容器自己调用完成初始化）***
* 设置超时时间为 10 分钟，这样就可以在一定时间间隔后重新从数据库读取数据
###### XML 配置
```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:cache="http://www.springframework.org/schema/cache"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/cache http://www.springframework.org/schema/cache/spring-cache.xsd">
    
        <!-- 配置缓存管理器，相当于 @EnableCaching 注解 -->
        <cache:annotation-driven cache-manager="redisCacheManager" />
    
        <!-- Spring 缓存管理器 -->
        <bean id="redisCacheManager" class="org.springframework.data.redis.cache.RedisCacheManager">
            <constructor-arg index="0" ref="redisTemplate" />
    
            <property name="defaultExpiration" value="600" />
    
            <property name="cacheNames">
                <list>
                    <value>redisCacheManager</value>
                </list>
            </property>
        </bean>
    
    
    
    
        <!-- RedisTemplate 配置-->
        
        <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
            <property name="maxIdle" value="50" />
            <property name="maxTotal" value="100" />
            <property name="maxWaitMillis" value="20000" />
        </bean>
    
    
        <bean id="connectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
            <property name="poolConfig" ref="poolConfig" />
            <property name="password" value="abcdefg" />
        </bean>
    
    
        <bean id="stringRedisSerializer" class="org.springframework.data.redis.serializer.StringRedisSerializer" />
        <bean id="redisTemplate" class="org.springframework.data.redis.core.RedisTemplate">
            <property name="connectionFactory" ref="connectionFactory" />
            <property name="defaultSerializer" ref="stringRedisSerializer" />
            <property name="keySerializer" ref="stringRedisSerializer" />
            <property name="valueSerializer" ref="stringRedisSerializer" />
        </bean>
    </beans>
```
#### 缓存注解
加在 Service 层实现类上
###### @Cacheable 和 @CachePut
简介
* @Cacheable：Spring 根据是否查找到 Redis 缓存决定直接读取还是执行方法，如果执行，将返回值储存到 Redis
* @CachePut：无论如何都执行方法，并将方法返回值储存到 Redis 

属性
* value：使用的缓存名称
* key：支持 Spring EL，计算对应的缓存 key（可以是 参数/返回值，或者进一步读取引用变量）

示例：
```java
    //缓存中有就读取缓存，否则执行方法
     @Cacheable(value = "redisCacheManager", key = "'redis_role_'+#id")  
    public Role getRole(Integer id) {
        return roleMapper.getRole(id);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
                                                                //读取返回值（不能用于 @Cacheable）
    @CachePut(value = "redisCacheManager", key = "'redis_role_'+#result.id")  //执行方法，更新缓存
    public Role insertRole(Role role) {
        roleMapper.insertRole(role);
        //自动主键回填
        return role;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @CachePut(value = "redisCacheManager", key = "'redis_role_'+#role.id")  //执行方法，更新缓存
    public Role updateRole(Role role) {
        roleMapper.updateRole(role);
        return role;
    }
```
###### @CacheEvict
简介：删除指定的缓存 key

属性：
* value、key
* allEntries：删除 Redis 中所有缓存（慎用）
* beyondInvocation：在进入方法前/后删除缓存

示例：
```java
     @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
                                                                        //方法执行前删除缓存
    @CacheEvict(value = "redisCacheManager", key = "'redis_role_'+#id", beforeInvocation = true)
    public int deleteRole(Integer id) {
        return roleMapper.deleteRole(id);
    }
```

#### 不适用缓存
命中率不高，查询条件导致的返回结果多样
```java
    //不使用缓存，因为命中率不高，查询条件导致的返回结果多样
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public List<Role> findRoles(String roleName, String note) {
        return null;
    }
```

#### 自调用失效
因为缓存也采用 AOP 机制，所以也会产生自调用问题
