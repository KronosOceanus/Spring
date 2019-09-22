package springJedis;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

public class SpringRedisTest {

    //不同连接执行操作
    private void test01(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
        Role role = new Role();
        role.setId(1);
        role.setRoleName("cs");
        redisTemplate.opsForValue().set("role", role);
        Role role1 = (Role)redisTemplate.opsForValue().get("role");
        System.out.println(role1.getRoleName());
    }
    //前后使用同一个连接
    private void test02(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
        Role role = new Role();
        role.setId(1);
        role.setRoleName("cs");
        SessionCallback callback = new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.boundValueOps("role").set(role);
                return redisOperations.boundValueOps("role").get();
            }
        };
        Role role1 = (Role)redisTemplate.execute(callback);
        System.out.println(role1.getRoleName());
    }


    public static void main(String[] args) {
        SpringRedisTest srt = new SpringRedisTest();
        srt.test02();

    }
}
