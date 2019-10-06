import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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
    // set 测试
    private void test06(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);

        Set set = null;
        // sadd key member1 [member2]
        redisTemplate.boundSetOps("set1").add("v1","v2","v3","v4","v5","v6");
        redisTemplate.boundSetOps("set2").add("v0","v2","v4","v8");
        // scard key
        redisTemplate.opsForSet().size("set1");
        // sdiff / sinter key1 key2，差集
        set = redisTemplate.opsForSet().difference("set1", "set2");
        set = redisTemplate.opsForSet().intersect("set1", "set2");
        // sismember key member，交集
        boolean exists = redisTemplate.opsForSet().isMember("set1","v1");
        // smembers key，返回所有 member
        set = redisTemplate.opsForSet().members("set1");
        // spop key，随机弹出
        String val = (String)redisTemplate.opsForSet().pop("set1");
        // srandmember key，随机获取
        val = (String)redisTemplate.opsForSet().randomMember("set1");
        // srandmember key count，随机获取 count 个
        List list = redisTemplate.opsForSet().randomMembers("set1", 2L);
        // srem key member1 [member2]
        redisTemplate.opsForSet().remove("set1", "v1");
        // sunion key1 key2，并集
        redisTemplate.opsForSet().union("set1", "set2");
        // sdiffstore / interstore / unionstore des key1 key2，差/交/并集，并存放在 des 中
        redisTemplate.opsForSet().differenceAndStore("set1", "set2", "diff_set");
        redisTemplate.opsForSet().intersectAndStore("set1", "set2", "inter_set");
        redisTemplate.opsForSet().unionAndStore("set1", "set2", "union_set");

        printSet(set);
    }
    // zset 测试
    private void test07(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);

        Set<ZSetOperations.TypedTuple> set1 = new HashSet<>();
        Set<ZSetOperations.TypedTuple> set2 = new HashSet<>();

        //初始化
        int j=9;
        for (int i=1;i<=9;i++){
            j--;

            Double score1 = (double) i;
            String value1 = "x" + i;
            Double score2 = (double) j;
            String value2 = j%2 == 0 ? "y" + j : "x" + j;
            ZSetOperations.TypedTuple typedTuple1 = new DefaultTypedTuple(value1, score1);
            set1.add(typedTuple1);
            ZSetOperations.TypedTuple typedTuple2 = new DefaultTypedTuple(value2, score2);
            set2.add(typedTuple2);
        }
        // zadd key value + score
        redisTemplate.opsForZSet().add("zset1", set1);
        redisTemplate.opsForZSet().add("zset2", set2);
        // zcard key
        Long size = redisTemplate.opsForZSet().size("zset1");
        System.err.println("size = " + size);
        // zcount key min max，统计 key 中 score >= min && <= max 的 member 个数
        Long count = redisTemplate.opsForZSet().count("zset1", 3, 6);
        System.err.println("count = " + count);

        Set set = null;
        // zrange key start stop，根据分值范围取子集
        set = redisTemplate.opsForZSet().range("zset1", 1, 5);
        printSet(set);
        //获取集合所有元素（value + score），并按照分数排序（-1 代表所有几何）
        set = redisTemplate.opsForZSet().rangeWithScores("zset1", 0, -1);
        printTypedTuple(set);
        // zinterstore des key1 key2，求交集并放入 des 中
        size = redisTemplate.opsForZSet().intersectAndStore("zset1", "zset2", "inter_zset");
        System.err.println(size);

        //区间
        RedisZSetCommands.Range range = RedisZSetCommands.Range.range();
        range.lt("x8");
        range.gt("x1");
        // zrangebylex key min + max，获取范围之内的元素 value
        set = redisTemplate.opsForZSet().rangeByLex("zset1", range);
        printSet(set);

        range.lte("x8");
        range.gte("x1");
        set = redisTemplate.opsForZSet().rangeByLex("zset1", range);
        printSet(set);

        RedisZSetCommands.Limit limit = RedisZSetCommands.Limit.limit();
        limit.offset(5);
        limit.count(4);
        // 从 offset + 1 个开始，取 count 个 member 的 value
        // zrangebylex key min + max limit offset + count
        set = redisTemplate.opsForZSet().rangeByLex("zset1", range, limit);
        printSet(set);
        // zrank key member，计算 member 的排行
        Long rank = redisTemplate.opsForZSet().rank("zset1", "x4");
        System.err.println("rank = " + rank);
        //删除目标 member，返回删除数
        size = redisTemplate.opsForZSet().remove("zset1", "x5", "x6");
        System.err.println("delete = " + size);
        // zremrangebyrank key start stop，删除排名为 [start + 1 ,stop + 1] 的 member
        size = redisTemplate.opsForZSet().removeRange("zset2", 0, 3);
        System.err.println(size);
        //获取所有元素
        set = redisTemplate.opsForZSet().rangeWithScores("zset2", 0, -1);
        printTypedTuple(set);

        size = redisTemplate.opsForZSet().remove("zset2","y6", "y3");
        System.err.println(size);
        // zincrby key member increment，给 member 的 score 加上 increment，返回计算后的 score
        Double db1 = redisTemplate.opsForZSet().incrementScore("zset1", "x1", 11);
        System.err.println(db1);

        redisTemplate.opsForZSet().removeRangeByScore("zset1", 1, 2);
        //由大到小获取所有元素
        // zrevrangebyscore key max min withscores
        set = redisTemplate.opsForZSet().reverseRangeWithScores("zset2", 1, 10);
        printTypedTuple(set);
    }
    // hyperloglog 测试
    private void test08(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
        // pfadd key element
        redisTemplate.opsForHyperLogLog().add("HyperLogLog","a","b","c","d","a");
        redisTemplate.opsForHyperLogLog().add("HyperLogLog2","a");
        redisTemplate.opsForHyperLogLog().add("HyperLogLog2", "z");
        // pfcount key
        Long size = redisTemplate.opsForHyperLogLog().size("HyperLogLog");
        System.err.println(size);

        size = redisTemplate.opsForHyperLogLog().size("HyperLogLog2");
        System.err.println(size);
        // pfmerge des key1 key2...
        redisTemplate.opsForHyperLogLog().union("des_key", "HyperLogLog", "HyperLogLog2");
        size = redisTemplate.opsForHyperLogLog().size("des_key");
        System.err.println(size);
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
    //打印 TypedTuple 集合
    public void printTypedTuple(Set<ZSetOperations.TypedTuple> set){
        if (set != null && set.isEmpty()){
            return;
        }
        for (ZSetOperations.TypedTuple val : set) {
            System.err.println("{ value = " + val.getValue() + ", score = " + val.getScore() + " }");
        }
    }
    //打印 set 集合
    public void printSet(Set set){
        if (set != null && set.isEmpty()){
            return;
        }
        for (Object val : set){
            System.err.print(val + "\t");
        }
        System.err.println();
    }

    public static void main(String[] args) throws SerializationException {
        Test t = new Test();
        t.test08();
    }
}
