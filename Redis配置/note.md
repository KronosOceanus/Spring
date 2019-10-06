[TOC]
# Redis 配置
## Redis 备份（持久化）
#### 快照备份 ss
备份当前瞬间内存中的数据记录，配置：
* save 900 1：当 900s 执行 1 个写命令时，启用快照备份
* stop-writes-on-bgsave-error yes：异步写入碰到错误停止
* rdbchecksum yes：检验 ss 数据文件
* dbfilename dump.rdb：ss 数据文件
#### 只追加文件 AOF
将执行过的写命令依次保存在 Redis 文件中，将来依次执行被保存的命令，配置：
* appendonly on：不启用 AOF 方式备份
* appendfilename "appendonly.aof"：AOF 命令备份文件
* appendfsync always/everysec/no：命令和文件同步频率，执行命令时/每秒一次（默认）/不 同步
* no-appendfsync-on-rewrite：在后台 AOF 文件重写时调用 fsync ？
* auto-aof-rewrite-percentage 100：重写 AOF 文件条件，文件大小增量为 100% 的时候
* auto-aof-rewrite-min-size 64mb：重写 AOF 文件条件，文件大小超过 64mb
* aof-load-truncated yes：意外中断后是否能恢复写入 

## Redis 内存回收策略
Redis 回收垃圾期间也会造成系统缓慢
#### maxmemory-policy
六种回收策略：
* volatile-lru：淘汰超时 key-value 对中最近使用最少的
* allkeys-lru：淘汰所有 key-value 对中最近使用最少的
* volatile-random：随机淘汰超时的 key-value 对
* allkeys-random：随机淘汰所有 key-value 对
* volatile-ttl：删除存活时间最短的 key-value
* noeviction（默认）：不淘汰
* **LRU 和 TTL 算法都不是很精确的算法**
#### maxmemory-samples
比较时选取的 key-value 对样本数

## 主从同步
#### 简介
* 多台数据服务器中，只有一台主服务器，只负责写入数据，不负责让外部程序读取
* 存在多台从服务器。不写入数据，只负责同步主服务器的数据，让外部程序读取
* 主服务器写入数据后，立刻将写入数据的命令发送给从服务器，从而完成主从同步
* 应用程序可以从任意一台从服务器读取数据
* 当主服务器不能工作，就从从服务器中选举一台当主服务器
#### 配置
* dir：默认是 ./ ，
* dbfilename：默认是 dump.rdb
表示采用 Redis 主服务器当前目录的 dump.rdb 文件进行同步（必须保证存在）
* slaveof：从服务器配置，指明主服务器和端口
* slaveof no one：在从服务器的 Redis 命令客户端发送该命令，表示不想让从服务器继续复制主服务器的数据
#### 过程
* 启动主服务器，使从服务器可以通过命令或者重启配置项同步到主服务器
* 启动从服务器，读取同步配置，根据配置决定是否使用当前数据响应客户端，然后发送 sync 命令
* 主服务器接受 sync 命令，执行 bgsave 备份数据，然后发送快照给从服务器
    主服务器此时将从客户端读取的数据写入缓冲区
* 如果从服务器未收到快照文件，则根据配置决定是否使用现有数据响应客户端或拒绝
* 如果收到快照文件，从服务器丢失现有数据，开始读取快照文件
* 从服务器执行快照文件命令，主服务器发送执行 bg…… 期间写入缓冲区的命令，从服务器接受
* 之后同步写入命令，完成主从同步

## 哨兵模式
#### 简介
哨兵是一个独立进程，通过发送命令等待 Redis 服务器响应来监控 Redis
#### 作用
* 检测到 master 宕机时，会自动将 slave 切换成 master，并通过发布订阅模式通知其他从服务器，修改配置文件
* 还可以采用多个哨兵（每个服务器上一个/还可以互相监控），在 master 宕机后，通过投票选举下一台 master
#### 搭建
* 主服务器 redis.conf
```editorconfig
    # 跨网络访问
    bind 0.0.0.0
    # 设置密码
    requirepass "abcdefg"
```
* 从服务器 redis.conf，除了上面两项，增加了如下配置
```editorconfig
    # 从属主机 IP 端口
    slaveof 192.168.11.128 6379
    # 从属主机密码
    masterauth abcdefg
```
* 哨兵配置 sentinel.conf（每个服务器都要）
```editorconfig
    # 禁用保护模式，方便测试
    protected-mode no
    # 主要配置       主机名    IP            端口 投票数
    sentinel monitor mymaster 192.168.11.128 6379 2
    #                   主机名   密码
    sentinel auth-pass mymaster abcdefg
```
* 启动（注意启动顺序），在 Redis 目录下执行
```editorconfig
    # 启动哨兵
    ./redis-sentinel ../sentinel.conf
    # 启动 Redis 服务器
    ./redis-server ../redis.conf
```
***启动顺序：主机，从机，哨兵***
#### Spring IoC XML 配置
```xml
    <!-- 哨兵模式配置（注入 connectionFactory / 必须用构造器注入） -->
    <bean id="sentinelConfig" class="org.springframework.data.redis.connection.RedisSentinelConfiguration">
        <!-- 服务名 -->
        <property name="master">
            <bean class="org.springframework.data.redis.connection.RedisNode">
                <!-- 主机名 -->
                <property name="name" value="mymaster" />
            </bean>
        </property>

        <!-- 哨兵 -->
        <property name="sentinels">
            <set>
                <!-- 哨兵 -->
                <bean class="org.springframework.data.redis.connection.RedisNode">
                    <!-- 必须用构造器注入 -->
                    <constructor-arg name="host" value="192.168.11.128" />
                    <constructor-arg name="port" value="6379" />
                </bean>
                <bean class="org.springframework.data.redis.connection.RedisNode">
                    <constructor-arg name="host" value="192.168.11.129" />
                    <constructor-arg name="port" value="6379" />
                </bean>
                <bean class="org.springframework.data.redis.connection.RedisNode">
                    <constructor-arg name="host" value="192.168.11.130" />
                    <constructor-arg name="port" value="6379" />
                </bean>
            </set>
        </property>
    </bean>


    <bean id="connectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
        <constructor-arg name="sentinelConfig" ref="sentinelConfig" />
        <property name="poolConfig" ref="poolConfig" />
        <property name="password" value="abcdefg" />
    </bean>
```
#### 其他配置项
* port：哨兵端口
* dir <文件夹路径>：哨兵临时文件夹，默认./tmp，要保证有写入权限
* sentineldown-after-milliseconds <服务名><毫秒数>：单个哨兵认定主机宕机时间
* sentinel parallel-syncs <服务名><服务器数>：指定多少 Redis 服务器可以同步主机
* sentinel failover-timeout <服务名><毫秒数>：故障切换超时时间
* sentinel notification-script <服务名><脚本路径>：异常警告脚本