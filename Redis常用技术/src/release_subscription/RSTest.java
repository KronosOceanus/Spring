package release_subscription;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class RSTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void demo01(){
        String channel = "chat";
        redisTemplate.convertAndSend(channel, "shit!");
    }
}
