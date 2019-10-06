package jedis;

import redis.clients.jedis.Jedis;

public class StringRedisTest {

    //性能测试（缺点，只能提供字符串操作）
    public static void main(String[] args) {
        int i = 0;
        try(
                Jedis jedis = new Jedis("localhost", 6379);
                // 密码 jedis.auth("password");
        ){
            long start = System.currentTimeMillis();
            while(true){
                long end = System.currentTimeMillis();
                if (end - start >= 1000){
                    break;
                }
                i++;
                jedis.set("test" + i, i + "");
            }
        }
        System.out.println("redis 每秒操作：" + i + "次");
    }
}
