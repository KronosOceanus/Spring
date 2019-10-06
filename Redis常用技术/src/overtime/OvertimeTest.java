package overtime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.swing.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class OvertimeTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void demo01(){
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
    }
}
