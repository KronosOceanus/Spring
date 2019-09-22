[TOC]
# Redis 概述
Redis 速度快，支持事务、发布订阅消息模式、主从复制、持久化等功能
## java 中使用 redis
#### 导入 jedis.jar 包
#### 测试性能
```java
    int i = 0;
        try(
                Jedis jedis = new Jedis("localhost", 6379);
                // 密码 jedis.auth("password");
        ){
            long start = System.currentTimeMillis();
            while(true){
                long end = System.currentTimeMillis();
                if (end - start >= 1000){
                    break;
                }
                i++;
                jedis.set("test" + i, i + "");
            }
        }
        System.out.println("redis 每秒操作：" + i + "次");
```

## Spring 中使用 redis
Spring 提供了很多序列化器可以完成各种对象的转换
#### 导入包
jedis.jar, spring-data-redis.jar, spring-data-commons.jar（注意版本对应问题）
#### XML 配置
```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <!-- 连接池配置 -->
        <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
            <!-- 最大空闲数，最大连接数，最大等待时间 -->
            <property name="maxIdle" value="50" />
            <property name="maxTotal" value="100" />
            <property name="maxWaitMillis" value="20000" />
        </bean>
        <!-- 要使用 RedisTemplate 而配置的连接工厂（需要传入连接池） -->
        <bean id="connectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
            <property name="poolConfig" ref="poolConfig" />
            <property name="hostName" value="localhost" />
            <property name="port" value="6379" />
            <!-- 还可以配置密码 password -->
        </bean>
    
        <!-- 配置序列化器 -->
        <bean id="jdkSerializationRedisSerializer" class="org.springframework.data.redis.serializer.JdkSerializationRedisSerializer" />
        <bean id="stringRedisSerializer" class="org.springframework.data.redis.serializer.StringRedisSerializer" />
    
        <!-- 配置 RedisTemplate 对象 -->
        <bean id="redisTemplate" class="org.springframework.data.redis.core.RedisTemplate">
            <!-- 注意，配置不同的序列化器 -->
            <property name="keySerializer" ref="stringRedisSerializer" />
            <property name="valueSerializer" ref="jdkSerializationRedisSerializer" />
            <property name="connectionFactory" ref="connectionFactory" />
        </bean>
    </beans>
```
#### 测试
* 不能保证每次使用 RedisTemplate 操作的是同一个连接
```java
    ApplicationContext applicationContext =
            new ClassPathXmlApplicationContext("applicationContext.xml");
    RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
    Role role = new Role();
    role.setId(1);
    role.setRoleName("cs");
    redisTemplate.opsForValue().set("role", role);
    Role role1 = (Role)redisTemplate.opsForValue().get("role");
    System.out.println(role1.getRoleName());
```
* 使用回调解决上述问题 
```java
    ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
    RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
    Role role = new Role();
    role.setId(1);
    role.setRoleName("cs");
    SessionCallback callback = new SessionCallback() {
        @Override
        public Object execute(RedisOperations redisOperations) throws DataAccessException {
            redisOperations.boundValueOps("role").set(role);
            return redisOperations.boundValueOps("role").get();
        }
    };
    Role role1 = (Role)redisTemplate.execute(callback);
    System.out.println(role1.getRoleName());
```

## 6 种数据类型
#### String
可以保存字符串、整数和浮点数
* 操作和计算
#### List
是一个链表，每个节点都包含一个字符串
* 丛链表两端插入或弹出
* 读取一个或者多个节点
* 根据多个条件删除或查找
#### Set 
无序集合，元素唯一
* 新增、读取、删除单个元素
* 包含
* 计算交集、并集、差集
* 随机读取元素
#### Hash
类似于 Map
* 增删查改单个键值对
* 获取所有键值对
#### Zset
有序集合，可以包含字符串、整数、浮点数、分值（按照分值排序）
* 增删查改元素
* 根据分值范围获取对应元素
#### HyperLogLog
基数，计算重复的值，以确定存储数量
* 只提供基数运算，不提供返回值
