[TOC]
# Redis 常用技术
## Redis 事务
开启事务之后，各种命令进入队列，执行事务一次发送队列命令去执行
#### 命令
* multi：开启事务
* watch key1 [key2...]：监听某些 key，当这些 key 在执行事务期间被修改，则回滚
* unwatch key1 [key2...]：取消监听
* exec：执行事务
* discard：回滚事务

#### 代码实现 （RedisTemplate 使用 Junit 注入）
```java
    //不用理会泛型警告
    SessionCallback callback = (SessionCallback) (RedisOperations ops) -> {
        // multi，开启事务
        ops.multi();
        ops.boundValueOps("key1").set("value1");
        //事务过程中，命令进入队列，但没有被执行，所以 value1 为空
        String value1 = (String)ops.boundValueOps("key1").get();
        System.out.println(value1);
        // exec，执行事务，之前的结果会以 List 形式返回，也可以直接将 List 返回到 SessionCallback
        List list = ops.exec();
        //事务结束后获取 value1
        value1 = (String)redisTemplate.opsForValue().get("key1");
        return value1;
    };
    String value = (String)redisTemplate.execute(callback);
    System.out.println(value);
```

#### 事务回滚
* 如果命令格式正确，数据类型错误，则不会影响事务的其他操作
* 命令格式错误，事务回滚

#### 监控事务
使用 watch key 监听某个键，参考了多线程中的 CAS（比较与交换，Compare And Swap）去执行的
* CAS：线程开始时读取多线程共享数据，并保存到当前线程的副本中，执行更新前，判断副本值和当前线程共享
    的值是否一致，一致则修改，否则回滚，但线程汇总 CAS 会产生 ABA 问题
* ABA 问题：处理复杂运算时，线程 2 修改的 X 值可能导致线程 1 的运算出错，但最后 X 又被修改为原来的值，
    ，导致提交成功，出错。X 经历了 A->B->A 的变化，所以叫做 ABA 问题，解决方案是新增并探测 
        version（修改次数）字段

#### 流水线
使用 multi / exec 命令是有开销的，因为他会检测对应的 key 和序列化命令，所以有时候会需要批量执行
    一系列命令，提高系统的性能，而性能瓶颈主要是网络的延迟，所以就有了流水线技术（是一种网络协议）
* 代码
```java
    //连接池获取连接
    Jedis jedis = new JedisPool(poolConfig, "localhost").getResource();
    long start = System.currentTimeMillis();
    //开启流水线
    Pipeline pipeline = jedis.pipelined();
    for (int i=0;i<100000;i++){
        pipeline.set("pipeline_key" + i, "pipeline_value" + i);
        pipeline.get("pipeline_key" + i);
    }
    // pipeline.sync()，同步，但不返回结果
    //同步并返回所有的结果到 List
    List result = pipeline.syncAndReturnAll();
    long end = System.currentTimeMillis();
    System.out.println(end - start);
```
系统性能大幅度提高
#### 流水线 + 事务
```java
    SessionCallback callback = (SessionCallback)(RedisOperations ops)->{
        for (int i=0;i<100000;i++){
            int j = i + 1;
            ops.boundValueOps("pipeline_key" + j).set("pipeline_value" + j);
            ops.boundValueOps("pipeline_key" + j).get();
        }
        return null;
    };
    long start = System.currentTimeMillis();
    List result = redisTemplate.executePipelined(callback);
    long end = System.currentTimeMillis();
    System.out.println(end - start);
```

## 发布订阅
订阅某个渠道的用户，一旦渠道发布信息，就会接收到
#### 过程
* SUBSCRIBE chat：客户端订阅 chat 渠道的消息
* publish chat "let's go!"：发布消息
* 收到消息
#### 实现
###### 监听类（要实现 MessageListener 接口）
* 类
```java
    //发布订阅监听类
    public class RedisMessageListener implements MessageListener {
    
        private RedisTemplate redisTemplate;
    
        @Override
        public void onMessage(Message message, byte[] bytes) {
            byte[] body = message.getBody();
            String msgBody = (String)redisTemplate.getValueSerializer().deserialize(body);
            System.err.println(msgBody);
    
            byte[] channel = message.getChannel();
            String channelStr = (String)redisTemplate.getStringSerializer().deserialize(channel);
            System.err.println(channelStr);
    
            String bytesStr = new String(bytes);
            System.err.println(bytesStr);
        }
    
    
    
    
        public void setRedisTemplate(RedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
        }
    }
```
* XML 配置 bean
```xml
    <!-- 监听类 -->
    <bean id="redisMsgListener" class="release_subscription.RedisMessageListener">
        <property name="redisTemplate" ref="redisTemplate" />
    </bean>
```
###### 监听容器 XML 配置
```xml
    <!-- 监听容器 -->
    <bean id="topicContainer" class="org.springframework.data.redis.listener.RedisMessageListenerContainer"
        destroy-method="destroy">
        <property name="connectionFactory" ref="connectionFactory" />
        <property name="taskExecutor">
            <bean class="org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler">
                <property name="poolSize" value="3" />
            </bean>
        </property>
        <!-- 消息监听 map -->
        <property name="messageListeners">
            <map>
                <entry key-ref="redisMsgListener">
                    <bean class="org.springframework.data.redis.listener.ChannelTopic">
                        <!-- 渠道名称 -->
                        <constructor-arg value="chat" />
                    </bean>
                </entry>
            </map>
        </property>
    </bean>
```
###### Junit 测试
```java
    public void demo01(){
        String channel = "chat";
        redisTemplate.convertAndSend(channel, "shit!");
    }
```

## 超时命令
类似 JVM 垃圾回收一样，Redis 也需要回收垃圾（一般是超时的 key-value 对）
#### 代码/命令
```java
    redisTemplate.execute((RedisOperations ops) -> {
        ops.boundValueOps("key1").set("value1");
        String keyValue = (String)ops.boundValueOps("key1").get();

        // ttl key，查看 key 的超时时间
        // -1 表示没有超时时间，-2 表示 key 不存在或者已超时
        long expSecond = ops.getExpire("key1");
        System.out.println(expSecond);

        boolean b = false;
        // expire key seconds，设置超时时间
        b = ops.expire("key1", 120L, TimeUnit.SECONDS);
        // persist key，持久化 key，取消超时
        b = ops.persist("key1");
        long l = 0L;
        //取消超时后，时间变为 -1
        l = ops.getExpire("key1");
        System.out.println(l);

         long now = System.currentTimeMillis();
         Date date = new Date();
         date.setTime(now + 120000);
         // expireat key timestamp，设置超时时间点
         ops.expireAt("key1", date);
         return null;
    });
```
#### 回收策略
* Redis 的 key 超时不会自动回收，只会标记
* 定时回收：在某个确定时间触发一段代码，回收
* 惰性回收：当一个超市的 key，再次被 get 命令访问时，将触发 Redis 回收
* 一般选择在没有事务发生的时候触发 Redis 定时回收

## 使用 Lua 语言
#### java 中使用 lua
```java
    //简单 lua 脚本，直接使用 Jedis
    Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
    //执行脚本
    String helloJava = (String)jedis.eval("return 'hello java'");
    System.out.println(helloJava);
    //有参脚本
    jedis.eval("redis.call('set',KEYS[1], ARGV[1])", 1, "lua-key", "lua-value");
    String luaKey = (String) jedis.get("lua-key");
    System.out.println(luaKey);
    //得到标识，通过标识执行脚本
    String shal = jedis.scriptLoad("redis.call('set',KEYS[1], ARGV[1])");
    //也可以直接传入数组参数
    jedis.evalsha(shal, 1, new String[]{"sha-key", "sha-value"});
    String shalKey = jedis.get("sha-key");
    System.out.println(shalKey);
```
#### lua 脚本存储对象
* Spring 提供了有关脚本的接口 RedisScript，默认实现类 DefaultRedisScript（封装一个脚本），可以通过该
    对象设置命令、获取标识、设置结果类型等
* 要求被存储的对象必须能**序列化**
* 执行脚本只需要传递 SHA-1 签名算法返回的32位字符串，提高传输效率
```java
    //默认脚本封装类
    DefaultRedisScript<Role> redisScript = new DefaultRedisScript<>();
    //设置脚本内容
    redisScript.setScriptText("redis.call('set', KEYS[1], ARGV[1]) " +
            "return redis.call('get', KEYS[1])");

    // key 列表
    List<String> keyList = new ArrayList<>();
    keyList.add("role1");
    // 参数（/列表）
    Role role = new Role();
    role.setId(1);
    role.setRoleName("cs");
    //脚本标识
    String shal = redisScript.getSha1();
    System.out.println(shal);
    //设置结果类型
    redisScript.setResultType(Role.class);
    JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
    //执行脚本，参数（脚本，参数序列化器， 结果序列化器， key 列表， 参数列表）
    Role obj = (Role)redisTemplate.execute(redisScript, serializer, serializer, keyList, role);
    System.out.println(obj);
```
#### 执行 lua 文件
* lua 脚本
```lua
redis.call('set', KEYS[1], ARGV[1])
redis.call('set', KEYS[2], ARGV[2])

local n1 = tonumber(redis.call('get',KEYS[1]))
local n2 = tonumber(redis.call('get',KEYS[2]))

if n1 > n2 then
    return 1
end
if n1 == n2 then
    return 0
end
if n1 < n2 then
    return 2
end
```
###### 系统中执行
* 示例：在 Redis 目录下执行以下命令，redis-cli --eval test.lua 'key1' 'key2' , 2 4
* 其中逗号前后空格不能省略
###### 使用 evalsha 执行
```java
     File file = new File("D:\\project\\Spring\\Redis常用技术\\src\\lua\\test.lua");
    //将脚本文件转化为二进制流
    byte[] bytes = getFileToByte(file);
    Jedis jedis = (Jedis)redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
    //通过二进制流加载脚本文件并获取标识
    byte[] shal = jedis.scriptLoad(bytes);
    //执行脚本
    Object obj = jedis.evalsha(shal, 2, "key1".getBytes(), "key2".getBytes(),
            "2".getBytes(), "4".getBytes());
    System.out.println(obj);
```
* 辅助方法（将文件转化为二进制流）
```java
    //把文件转化为二进制数组
    private static byte[] getFileToByte(File file){
        byte[] result = new byte[(int) file.length()];
        try{
            InputStream is = new FileInputStream(file);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] bb = new byte[2048];
            int hasRead = -1;
            while((hasRead =  is.read(bb)) != -1){
                byteStream.write(bb, 0, hasRead);
            }
            result = byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
```
