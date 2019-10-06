package lua;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class LuaTest {

    @Autowired
    private RedisTemplate redisTemplate;

    // Jedis 测试 lua 脚本
    @Test
    public void demo01(){
        //简单 lua 脚本，直接使用 Jedis
        Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        //执行脚本
        String helloJava = (String)jedis.eval("return 'hello java'");
        System.out.println(helloJava);
        //有参脚本
        jedis.eval("redis.call('set',KEYS[1], ARGV[1])", 1, "lua-key", "lua-value");
        String luaKey = (String) jedis.get("lua-key");
        System.out.println(luaKey);
        //得到标识，通过标识执行脚本
        String shal = jedis.scriptLoad("redis.call('set',KEYS[1], ARGV[1])");
        //也可以直接传入数组参数
        jedis.evalsha(shal, 1, new String[]{"sha-key", "sha-value"});
        String shalKey = jedis.get("sha-key");
        System.out.println(shalKey);
    }

    // lua 脚本存储对象
    @Test
    public void demo02(){
        //默认脚本封装类
        DefaultRedisScript<Role> redisScript = new DefaultRedisScript<>();
        //设置脚本内容
        redisScript.setScriptText("redis.call('set', KEYS[1], ARGV[1]) " +
                "return redis.call('get', KEYS[1])");

        // key 列表
        List<String> keyList = new ArrayList<>();
        keyList.add("role1");
        // 参数（/列表）
        Role role = new Role();
        role.setId(1);
        role.setRoleName("cs");
        //脚本标识
        String shal = redisScript.getSha1();
        System.out.println(shal);
        //设置结果类型
        redisScript.setResultType(Role.class);
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
        //执行脚本，参数（脚本，参数序列化器， 结果序列化器， key 列表， 参数列表）
        Role obj = (Role)redisTemplate.execute(redisScript, serializer, serializer, keyList, role);
        System.out.println(obj);
    }

    // lua 脚本文件测试
    /**
     * 示例命令（在 redis 目录下执行）
     * redis-cli --eval test.lua 'key1' 'key2' , 2 4
     */
    @Test
    public void demo03(){
        File file = new File("D:\\project\\Spring\\Redis常用技术\\src\\lua\\test.lua");
        //将脚本文件转化为二进制流
        byte[] bytes = getFileToByte(file);
        Jedis jedis = (Jedis)redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        //通过二进制流加载脚本文件并获取标识
        byte[] shal = jedis.scriptLoad(bytes);
        //执行脚本
        Object obj = jedis.evalsha(shal, 2, "key1".getBytes(), "key2".getBytes(),
                "2".getBytes(), "4".getBytes());
        System.out.println(obj);
    }


    //把文件转化为二进制数组
    private static byte[] getFileToByte(File file){
        byte[] result = new byte[(int) file.length()];
        try{
            InputStream is = new FileInputStream(file);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] bb = new byte[2048];
            int hasRead = -1;
            while((hasRead =  is.read(bb)) != -1){
                byteStream.write(bb, 0, hasRead);
            }
            result = byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
