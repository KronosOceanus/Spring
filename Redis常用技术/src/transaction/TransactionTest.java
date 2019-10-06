package transaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TransactionTest {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JedisPoolConfig poolConfig;

    //事务
    @Test
    public void demo01(){
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
    }
    //流水线
    @Test
    public void demo02(){
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
    }
    //流水线 + 事务
    @Test
    public void demo03(){
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
    }
}
