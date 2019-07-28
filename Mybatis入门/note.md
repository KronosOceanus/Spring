[TOC]

# Mybatis
参数自动映射到 PreparedStatement 中，结果集映射为 java 对象

## Mybatis 配置文件
* SqlMapConfig.xml：
Mybatis 的全局配置文件，配置数据源，事务等 Mybatis 环境
* mappper.xml：
配置映射文件（配置 sql 语句）

## 核心 API
#### SqlSessionFactoryBuilder
根据配置/代码生成 SqlSessionFactory，采用的是分步构建的 Builder 模式，具体分步在 Configuration 类中完成
#### SqlSessionFactory（工厂接口）
生成 SqlSession，采用工厂模式，每个 Mybatis 应用都以一个 SqlSessionFactory 的实例为中心，实现类：
###### DefaultSqlSessionFactory
默认实现
###### SqlSessionManager
使用在多线程环境中，具体实现依赖默认实现
#### SqlSession（会话）
内部通过 Executor（执行器）来操作数据库，类似于 Connection 对象，作用：
* 发送 SQL 执行并返回结果（即操作数据库）
* 获取 Mapper 
* 控制数据库事务

实现类
###### DefaultSqlSession
默认实现
###### SqlSessionManager
使用在多线程环境中
#### Executor（执行器）
实现类：
###### BaseExecutor
基本执行器
###### CachingExecutor
缓存执行器
#### SQL Mapper（映射器）
由一个 java 接口和 XML 文件/注解构成，需要给出对应的 SQL 和映射规则
* 发送 SQL 去执行，并返回结果
#### MappedStatement（Mybatis 中底层封装对象）
包括 SQL 语句，输入参数、输出结果类型等

## 使用步骤
### 使用 XML 构建 SqlSessionFactory
* 全局配置文件 mybatis-config.xml
```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
    
    <configuration>
        <!-- 别名（方便引用） -->
        <typeAliases>
            <typeAlias type="entity.Role" alias="role" />
        </typeAliases>
        
        <!-- 数据库配置 -->
        <environments default="development">
            <environment id="development">
                <!-- 配置事务管理器，采用 JDBC 管理器方式 -->
                <transactionManager type="JDBC" />
                <!-- 配置数据库，采用连接池的方式 -->
                <dataSource type="POOLED">
                    <property name="driver" value="com.mysql.cj.jdbc.Driver" />
                    <property name="url" value="jdbc:mysql://127.0.0.1:3306/mybatis_intro
                                          ?useSSL=no&amp;serverTimezone=UTC" />
                    <property name="username" value="root" />
                    <property name="password" value="java521...." />
                </dataSource>
            </environment>
        </environments>
    
        <!-- 映射文件 -->
        <mappers>
            <mapper resource="mappers/RoleMapper.xml" />
        </mappers>
    </configuration>
```
* 使用以下代码生成 SqlSessionFactory
```java
    SqlSessionFactory sqlSessionFactory = null;
    String resource = "mybatis-config.xml";
    InputStream inputStream;
    try{
        //得到输入流
        inputStream = Resources.getResourceAsStream(resource);
        //利用 build 方法读取输入流创建 SqlSessionFactory
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return sqlSessionFactory;
```

### 映射器
* 编写一个 Bean
```java
    public class Role {
    
        private Integer id;
        private String roleName;
        private String note;
    }
```
#### XML 实现
* 编写接口（供 Mybatis 完成动态代理）
```java
    public interface RoleMapper {
        Role getRole(Integer id);
    }
```
* XML 配置
```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    
    <mapper namespace="mappers.RoleMapper">
        <!-- 查询语句 对应方法      参数类型           结果类型-->
        <select id="getRole" parameterType="integer" resultType="role">
            <!--              别名改写                                    参数 -->
            select id, role_name as roleName, note from t_role where id = #{id}
        </select>
    </mapper>
```
#### 注解实现
* 接口直接映射
```java
    public interface RoleMapper2 {
    
        @Select("select id, role_name as roleName, note from t_role where id = #{id}")
        Role getRole(Integer id);
    }
```
* 修改全局配置文件（mybatis-config.xml）内容
```xml
    <mappers>
        <mapper class="mappers.RoleMapper2" />  
    </mappers>
```
### 发送 SQL
#### SqlSession 发送 SQL
***注意！：事务默认关闭***
```java
    SqlSession sqlSession = sqlSessionFactory.openSession();
    //参数（全限定方法名，输入参数）     
    Role role = sqlSession.selectOne("getRole",1);
```
#### Mapper 发送 SQL
```java
    //获得接口
    RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
    Role role = roleMapper.getRole(1);
```

## 生命周期
#### SqlSessionFactoryBuilder
只能存在于创建 SqlSessionFactory 的方法中
#### SqlSessionFactory
相当于数据库连接池，一般使用单例，与整个 Mybatis 应用共存活
#### SqlSession
相当于连接，存活于一个业务请求中，用完应当及时关闭
#### Mapper
由 SqlSession 创建，生命周期至多和 SqlSession 保持一致，最好是在一个请求中

