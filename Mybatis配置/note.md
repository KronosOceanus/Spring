[TOC]

# Mybatis 配置
## 配置顺序
1. properties
2. settings
3. typeAliases
4. typeHandlers
5. objectFactory
6. plugins
7. environments
    * environment
        * transactionManager
        * dataSource
8. databaseIdProvider
9. mappers

## properties
* 在 property 子标签中书写配置
```xml
    <property name="database.driver" value="com.mysql.cj.jdbc.Driver" />
```
* 在 resource 中引入配置文件
```xml
    <properties resource="jdbc.properties" />
```
这两种方法之后可以通过占位符引用属性
* 使用程序传递（通过 props 将配置文件中的属性覆盖）
```java
    String resource = "mybatis-config.xml";
    SqlSessionFactory sqlSessionFactory = null;
    try(
            InputStream inputStream = Resources.getResourceAsStream(resource);
            InputStream in = Resources.getResourceAsStream("jdbc.properties");
    ){
        Properties props = new Properties();
        props.load(in);
        String username = props.getProperty("database.username");
        String password = props.getProperty("database.password");
        //解压用户名和密码，放入属性中
        props.put("database.username",CodeUtils.decode(username));
        props.put("database.password",CodeUtils.decode(password));
        //覆盖原先 properties 参数
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream,props);
    }catch (Exception e){
        e.printStackTrace();
    }
    return sqlSessionFactory;
```

## settings
全量配置
```xml
    <!-- 设置 -->
    <settings>
        <!-- 启动缓存 -->
        <setting name="cacheEnabled" value="true"/>
        <!-- 延迟加载 -->
        <setting name="lazyLoadingEnabled" value="true"/>
        <!-- 单一语句返回多个结果集 -->
        <setting name="multipleResultsetsEnabled" value="true"/>
        <!-- 列标签代替列名 -->
        <setting name="useColumnLabel" value="true"/>
        <!-- 允许 JDBC 自动生成主键 -->
        <setting name="useGeneratedKeys" value="false"/>
        <!-- 自动映射字段或属性的操作     自动映射（除了引用变量）-->
        <setting name="autoMappingBehavior" value="PARTIAL"/>
        <!-- 自动映射到未知列的操作，默认不处理     日志级别达到 warning 才显示日志-->
        <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
        <!-- 默认执行器类型                 普通执行器-->
        <setting name="defaultExecutorType" value="SIMPLE"/>
        <!-- 设置超时时间 -->
        <setting name="defaultStatementTimeout" value="25"/>
        <!-- 数据库驱动默认返回条数 -->
        <setting name="defaultFetchSize" value="100" />
        <!-- 允许在嵌套语句中使用分页 -->
        <setting name="safeRowBoundsEnabled" value="false"/>
        <!-- 开启自动驼峰命名规则映射 -->
        <setting name="mapUnderscoreToGamelCase" value="false"/>
        <!-- 缓存某个作用域中的所有查询结果 -->
        <setting name="localCacheScope" value="SESSION"/>
        <!-- 为 null 指定 JDBC 类型 -->
        <setting name="jdbcTypeForNull" value="OTHER"/>
        <!-- 指定对象方法触发一次延迟加载 -->
        <setting name="lazyLoadTriggerMethods" value="equals, clone, hashCode, toString"/>
    </settings>
```
## typeAliaes
#### 内置别名
#### 自定义别名
```xml
    <typeAlias>
        <typeAlias alias="role" type="entity.Role" />
    </typeAlias>
```
## typeHandler
###### TypeHandler 接口
转换器必须实现 TypeHandler 接口
```java
    public interface TypeHandler<T> {
        //为 preparedStatement 设置参数，输入到数据库（JavaType -> JdbcType）
        void setParameter(PreparedStatement var1, int var2, T var3, JdbcType var4) throws SQLException;
    
        //得到查询结果（JdbcType -> JavaType）
        T getResult(ResultSet var1, String var2) throws SQLException;
        T getResult(ResultSet var1, int var2) throws SQLException;
        T getResult(CallableStatement var1, int var2) throws SQLException;
    }
```
###### BaseTypeHandler 类
系统的转换器都继承 BaseTypeHandler 类，源码：
```java
    public abstract class BaseTypeHandler<T> extends TypeReference<T> implements TypeHandler<T> {
        /** @deprecated */
        @Deprecated
        protected Configuration configuration;
        public BaseTypeHandler() {
        }
        /** @deprecated */
        @Deprecated
        public void setConfiguration(Configuration c) {
            this.configuration = c;
        }
        //实现 TypeHandler 接口方法
        public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
            if (parameter == null) {
                if (jdbcType == null) {
                    throw new TypeException("JDBC requires that the JdbcType must be specified for all nullable parameters.");
                }
    
                try {
                    ps.setNull(i, jdbcType.TYPE_CODE);
                } catch (SQLException var7) {
                    throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. Cause: " + var7, var7);
                }
            } else {
                try {
                    this.setNonNullParameter(ps, i, parameter, jdbcType);
                } catch (Exception var6) {
                    throw new TypeException("Error setting non null for parameter #" + i + " with JdbcType " + jdbcType + " . Try setting a different JdbcType for this parameter or a different configuration property. Cause: " + var6, var6);
                }
            }
    
        }
        public T getResult(ResultSet rs, String columnName) throws SQLException {
            try {
                return this.getNullableResult(rs, columnName);
            } catch (Exception var4) {
                throw new ResultMapException("Error attempting to get column '" + columnName + "' from result set.  Cause: " + var4, var4);
            }
        }
        public T getResult(ResultSet rs, int columnIndex) throws SQLException {
            try {
                return this.getNullableResult(rs, columnIndex);
            } catch (Exception var4) {
                throw new ResultMapException("Error attempting to get column #" + columnIndex + " from result set.  Cause: " + var4, var4);
            }
        }
        public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
            try {
                return this.getNullableResult(cs, columnIndex);
            } catch (Exception var4) {
                throw new ResultMapException("Error attempting to get column #" + columnIndex + " from callable statement.  Cause: " + var4, var4);
            }
        }
    
        
        // 4 个抽象方法
        public abstract void setNonNullParameter(PreparedStatement var1, int var2, T var3, JdbcType var4) throws SQLException;
        public abstract T getNullableResult(ResultSet var1, String var2) throws SQLException;
        public abstract T getNullableResult(ResultSet var1, int var2) throws SQLException;
        public abstract T getNullableResult(CallableStatement var1, int var2) throws SQLException;
    }
```
分析：
* 是个抽象类，需要子类实现 4 个抽象方法，它本身实现了 TypeHandler 接口的 4 个方法
* getResult：非空结果集通过 getNullableResult 方法获取，如果为空则返回 null
* setParameter：参数 parameter 和 jdbcType 同时为空，抛出异常；只知道 jdbcType，则进行空设置；
    参数不为空，则调用 setNonNullParameter 方法设置参数
* getNullableResult：用于存储过程
#### 内置转换器
#### 自定义转换器
例：
```java
    /**
     * 使用包扫描（但是包扫描无法注册对应类型）
     * 通过注解注册对应类型
     */
    @MappedTypes(String.class)
    @MappedJdbcTypes(JdbcType.VARCHAR)
    public class StringTypeHandler implements TypeHandler<String> {
    
        //日志打印，（包名 org.apache.log4j.Logger）
        Logger logger = Logger.getLogger(StringTypeHandler.class);
    
        //？？？？？？？
        @Override
        public void setParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
            logger.info("设置 string 参数【" + s + "】");
            preparedStatement.setString(i,s);
        }
    
    
        /**
         * 查询时触发
         */
        @Override
        public String getResult(ResultSet resultSet, String s) throws SQLException {
            String result = resultSet.getString(s);
            logger.info("读取 string 参数1【" + result + "】");
            return result;
        }
        @Override
        public String getResult(ResultSet resultSet, int i) throws SQLException {
            String result = resultSet.getString(i);
            logger.info("读取 string 参数2【" + result + "】");
            return result;
        }
        @Override
        public String getResult(CallableStatement callableStatement, int i) throws SQLException {
            String result = callableStatement.getString(i);
            logger.info("读取 string 参数3【" + result + "】");
            return result;
        }
    }
```
定义完之后在 Mybatis 配置文件中注册
```xml
    <!-- 类型处理器 -->
    <typeHandlers>
        <typeHandler handler="type_handler.StringTypeHandler"
                     javaType="string" jdbcType="VARCHAR" />
    </typeHandlers>
```
也可以直接使用包扫描
```xml
    <!-- 使用包扫描类型处理器 -->
    <typeHandlertype>
        <package name="type_handler" />
    </typeHandlertype>
```
之后就可以在映射器中引用（不引用系统也会自动判断，因为已经注册过）
```xml
    <resultMap id="file" type="entity.TestFile">
        <id column="id" property="id" />
        <id column="content" property="content"
            typeHandler="org.apache.ibatis.type.BlobTypeHandler" />
    </resultMap>
```
#### 内置枚举转换器
* EnumOrdinalTypeHandler：将枚举对象按照数组下标形式储存
* EnumTypeHandler：将枚举对象按照对应名称储存
#### 自定义枚举转换器
使用方法与自定义转换器相同

#### 文件操作
* BlobTypeHandler：byte[]  <--->  blob
* BlobInputStreamTypeHandler：InputStream  <--->  blob

## ObjectFactory
Mybatis 使用该工厂创建结果集实例，一般继承系统实现好的 DefaultObjectFactory，然后重写一部分方法
    完成自己目的，例：
```java
    /**
     * 自定义（部分）对象工厂，用于创建结果集实例
     */
    public class MyObjectFactory extends DefaultObjectFactory {
    
        Logger logger = Logger.getLogger(MyObjectFactory.class);
    
        private Object tmp = null;
    
        @Override
        public void setProperties(Properties properties) {
            super.setProperties(properties);
            logger.info("初始化参数【" + properties + "】");
        }
    
        //方法 1（执行顺序）
        @Override
        public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
            T result = super.create(type, constructorArgTypes, constructorArgs);
            logger.info("创建对象：" + result);
            tmp = result;
            return result;
        }
        //方法 2
        @Override
        public <T> T create(Class<T> type) {
            T result = super.create(type);
            logger.info("创建对象：" + result);
            logger.info("是否和上次创建的是同一个对象：【" + (tmp == result) + "】" );
            return result;
        }
    
        @Override
        public <T> boolean isCollection(Class<T> type) {
            return super.isCollection(type);
        }
    }
```
执行过程：先执行方法 1，然后执行方法 2，生成两个对象，然后观察返回值是否是同一个对象，最后适配为一个对象

然后在 Mybatis 配置文件中配置
```xml
    <!-- 自定义对象工厂，用于的创建结果集实例 -->
    <objectFactory type="object_factory.MyObjectFactory">
        <property name="prop1" value="value1" />
    </objectFactory>
```
## envrionments
### transactionManager
#### Transaction 接口
```java
    public interface Transaction {
    
        Connection getConnection() throws SQLException;
    
        //主要作用
        void commit() throws SQLException;
        void rollback() throws SQLException;
        void close() throws SQLException;
    
        Integer getTimeout() throws SQLException;
    }
```
主要作用：提交、回滚、关闭事务

系统内置两个实现类（对应两个工厂）
###### JdbcTransaction（别名 JDBC）
由 JdbcTransactionFactory 生成，底层以 Jdbc 实现
###### ManagedTransaction（别名 MANAGED）
把事务交给容器处理，需要将 closeConnection 属性设置为 false，阻止默认关闭连接的行为

#### 自定义事务
先自定义事务工厂（配置的时候也是配置工厂）
```java
    /**
     * 每个事务管理器对应一个工厂
     * 配置事务管理器的 type 即工厂种类
     */
    public class MyTransactionFactory implements TransactionFactory {
    
        @Override
        public void setProperties(Properties props) {
    
        }
    
        @Override
        public Transaction newTransaction(Connection connection) {
            return new MyTransaction(connection);
        }
    
        @Override
        public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel transactionIsolationLevel, boolean b) {
            return new MyTransaction(dataSource, transactionIsolationLevel, b);
        }
    }
```
然后对应事务类
```java
    /**
     * 事务管理器主要作用：
     * 提交、回滚、关闭事务
     */
    public class MyTransaction extends JdbcTransaction implements Transaction {
    
        public MyTransaction(Connection connection){
            super(connection);
        }
        public MyTransaction(DataSource dataSource, TransactionIsolationLevel level,
                             boolean desiredAutoCommit){
            super(dataSource, level, desiredAutoCommit);
        }
    
        @Override
        public Connection getConnection() throws SQLException {
            return super.getConnection();
        }
    
        @Override
        public void commit() throws SQLException {
            super.commit();
        }
    
        @Override
        public void rollback() throws SQLException {
            super.rollback();
        }
    
        @Override
        public void close() throws SQLException {
            super.close();
        }
    
        @Override
        public Integer getTimeout() throws SQLException {
            return super.getTimeout();
        }
    }
```
最后在 Mybatis 配置文件中配置，***注意！：配置的是对应工厂***
```xml
    <transactionManager type="transaction.MyTransactionFactory" />
```
### dataSource
###### UNPOOLED
采用连接的方式，每次请求都会打开一个 Connection
属性：
* 数据库的 4 大属性
* defaultTransactionIsolationLevel：默认的连接事务隔离级别
###### POOLED

###### JNDI





    