import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Test {

    //字符串测试
    private void test01(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
        // set key value
        redisTemplate.opsForValue().set("key1", "value1");
        redisTemplate.opsForValue().set("key2", "value2");
        // get key
        String value1 = (String)redisTemplate.opsForValue().get("key1");
        System.out.println(value1);
        // del key
        redisTemplate.delete("key1");
        // strlen key
        Long length = redisTemplate.opsForValue().size("key2");
        System.out.println(length);
        // getset key value
        String oldValue2 = (String)redisTemplate.opsForValue().getAndSet("key2", "new_value2");
        System.out.println(oldValue2);

        String value2 = (String)redisTemplate.opsForValue().get("key2");
        System.out.println(value2);
        // getrange key start end 求子串
        String rangeValue2 = redisTemplate.opsForValue().get("key2", 0, 3);
        System.out.println(rangeValue2);
        // append key value 拼接字符串，并返回新的长度
        int newLen = redisTemplate.opsForValue().append("key2", "_app");
        System.out.println(newLen);

        String appendValue2 = (String)redisTemplate.opsForValue().get("key2");
        System.out.println(appendValue2);
    }
    //字符串运算测试
    private void test02() throws SerializationException {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);

        redisTemplate.opsForValue().set("i", "9");
        printCurrValue(redisTemplate, "i");
        //整数加法（还支持 long 和 double）
        // incr key / incrBy key increment
        redisTemplate.opsForValue().increment("i", 1);
        printCurrValue(redisTemplate, "i");
        //整数减法（i 必须是整数）
        //decr key
        redisTemplate.getConnectionFactory().getConnection().decr(
                //将 i 序列化为整数
                redisTemplate.getKeySerializer().serialize("i")
        );
        printCurrValue(redisTemplate, "i");
        //整数减法（i - 6）
        // decrBy key decrement
        redisTemplate.getConnectionFactory().getConnection().decrBy(
                redisTemplate.getKeySerializer().serialize("i"), 6
        );
        printCurrValue(redisTemplate, "i");
        // incrByfloat key increment
        //加浮点数
        redisTemplate.opsForValue().increment("i", 2.3);
        printCurrValue(redisTemplate, "i");
    }
    // hash 测试
    private void test03(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);

        String key = "hash";
        Map<String, String> map = new HashMap<>();
        map.put("f1", "val1");
        map.put("f2", "val2");
        // hmset key field1 value1 [field2 value2]，设置多个 field - value
        redisTemplate.opsForHash().putAll(key, map);
        // hset key field value，设置单个 field - value
        redisTemplate.opsForHash().put(key, "f3", "6");
        printValueForHash(redisTemplate, key, "f3");
        // hexists key field
        boolean exists = redisTemplate.opsForHash().hasKey(key, "f3");
        System.out.println(exists);
        // hgetall key，获取所有 field - value
        Map keyValMap = redisTemplate.opsForHash().entries(key);
        // hincrby key field increment
        redisTemplate.opsForHash().increment(key, "f3", 2);
        printValueForHash(redisTemplate, key, "f3");
        // hincrbyfloat key field increment
        redisTemplate.opsForHash().increment(key, "f3", 0.88);
        printValueForHash(redisTemplate, key, "f3");
        // hvals key，获取所有 value
        List valueList = redisTemplate.opsForHash().values(key);
        // hkeys key，获取所有 field
        Set keyList = redisTemplate.opsForHash().keys(key);

        List<String> fieldList = new ArrayList<>();
        fieldList.add("f1");
        fieldList.add("f2");
        // hmget key field1 [field2]，获取对应 value
        List valueList2 = redisTemplate.opsForHash().multiGet(key, keyList);
        // hsetnx key field value，不存在对应的 field 才设置
        boolean success = redisTemplate.opsForHash().putIfAbsent(key, "f4", "val4");
        System.out.println(success);
        // hdel key field1 [field2]
        Long result = redisTemplate.opsForHash().delete(key, "f1", "f2");
        System.out.println(result);
    }
    // list 测试
    private void test04(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);

        //删除链表，以便反复测试
        redisTemplate.delete("list");
        // lpush  key node1 [node2]
        redisTemplate.opsForList().leftPush("list", "node3");
        List<String> nodeList = new ArrayList<>();
        for (int i=2;i>=1;i--){
            nodeList.add("node" + i);
        }
        //插入集合
        redisTemplate.opsForList().leftPushAll("list", nodeList);
        redisTemplate.opsForList().rightPush("list", "node4");
        //lindex key index，读取下标处节点，从 0 开始算
        String node1 = (String)redisTemplate.opsForList().index("list", 0);
        //llen key, 求链表长度
        long size = redisTemplate.opsForList().size("list");
        // lpop key，删除第一个节点并返回
        String lpop = (String)redisTemplate.opsForList().leftPop("list");
        String rpop = (String)redisTemplate.opsForList().rightPop("list");

        //底层操作 linsert 命令，在 node2(pivot) 前插入(node)节点
        //linsert key before|after pivot node
        redisTemplate.getConnectionFactory().getConnection().lInsert(
                "list".getBytes(StandardCharsets.UTF_8),
                RedisListCommands.Position.AFTER,
                "node2".getBytes(StandardCharsets.UTF_8),
                "before_node".getBytes(StandardCharsets.UTF_8)
        );
        //lpushx list node，存在 list 则插入
        redisTemplate.opsForList().leftPushIfPresent("list", "head");
        redisTemplate.opsForList().rightPushIfPresent("list", "end");
        //lrange list start end，获取链表从 start 到 end 的节点 value
        List valueList = redisTemplate.opsForList().range("list", 0, 10);

        nodeList.clear();
        for (int i=1;i<=3;i++){
            nodeList.add("node");
        }
        redisTemplate.opsForList().leftPushAll("list", nodeList);
        // lrem list count value，删除从左到右 value = "node" 的 |count|（绝对值） 个节点
        redisTemplate.opsForList().remove("list", 3, "node");
        // lset key index node，设置下标为 index 的节点 value = "node"
        redisTemplate.opsForList().set("list", 0, "new_head_value");
        //输出链表
        printList(redisTemplate, "list");
    }
    // list 阻塞测试
    private void test05(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
        //清空数据，以便重复测试
        redisTemplate.delete("list1");
        redisTemplate.delete("list2");
        //初始化 list1
        List<String> nodeList = new ArrayList<>();
        for (int i=1;i<=5;i++){
            nodeList.add("node" + i);
        }
        redisTemplate.opsForList().leftPushAll("list1", nodeList);
        // Spring 使用参数超时时间来作为阻塞命令的区分（超时时间 1s）
        redisTemplate.opsForList().leftPop("list1", 1, TimeUnit.SECONDS);
        redisTemplate.opsForList().rightPop("list1", 1, TimeUnit.SECONDS);
        //初始化 list2
        nodeList.clear();
        for (int i=1;i<=3;i++){
            nodeList.add("node" + i);
        }
        redisTemplate.opsForList().leftPushAll("list2", nodeList);
        //弹出 list1 最右边的节点，插入 list2 左边
        redisTemplate.opsForList().rightPopAndLeftPush("list1", "list2");
        redisTemplate.opsForList().rightPopAndLeftPush("list1", "list2", 1, TimeUnit.SECONDS);

        printList(redisTemplate, "list1");
        printList(redisTemplate, "list2");

    }

    //根据 key 输出 value
    private void printCurrValue(RedisTemplate redisTemplatem, String key){
        String i = (String)redisTemplatem.opsForValue().get(key);
        System.out.println(i);
    }
    //根据 key + field 输出 value
    private void printValueForHash(RedisTemplate redisTemplate, String key, String field){
        Object value = redisTemplate.opsForHash().get(key, field);
        System.out.println(value);
    }
    //打印链表数据
    private void printList(RedisTemplate redisTemplate, String key){
        long size = redisTemplate.opsForList().size(key);
        List valueList = redisTemplate.opsForList().range(key, 0, size);
        System.out.println(valueList);
    }

    public static void main(String[] args) throws SerializationException {
        Test t = new Test();
        t.test05();
    }
}
