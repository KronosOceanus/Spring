package redis_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;

public class RedisTemplateServiceImpl implements RedisTemplateService{

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void execMultiCommand() {
        Object obj = redisTemplate.execute((RedisOperations ops) -> {
            ops.boundValueOps("key1").set("abc");
            ops.boundHashOps("hash").put("hash-key-1", "hash-value-1");
            return ops.boundValueOps("key1").get();
        });
        System.err.println(obj);
    }

    @Override
    public void execTransaction() {
        List list = (List)redisTemplate.execute((RedisOperations ops) -> {
            ops.watch("key1");
            //开启事务
            ops.multi();
            ops.boundValueOps("key1").set("abc");
            ops.boundHashOps("hash").put("hash-key-1", "hash-value-1");
            ops.opsForValue().get("key1");
            List result = ops.exec();
            return result;
        });
        System.err.println(list);
    }

    @Override
    public void execPipeline() {
        List list = redisTemplate.executePipelined(new SessionCallback() {
            @Override
            public Object execute(RedisOperations ops) throws DataAccessException {
                ops.boundValueOps("key1").set("abc");
                ops.boundHashOps("hash").put("hash-key-1", "hash-value-1");
                ops.opsForValue().get("key1");
                return null;
            }
        });
        System.err.println(list);
    }
}
