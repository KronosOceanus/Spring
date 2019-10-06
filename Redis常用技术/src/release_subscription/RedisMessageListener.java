package release_subscription;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

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
