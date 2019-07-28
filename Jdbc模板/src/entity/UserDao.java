package entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 还可以继承 JdbcDaoSupport
 * 然后在配置文件中配置 JdbcTemplate / DataSource（父类使用这俩之一创建 JdbcTemplate）
 */
@Repository("userDao")
public class UserDao {

    //在 Dao 层添加模板
    private final JdbcTemplate jdbcTemplate;
    //使用构造器注入
    @Autowired
    public UserDao(@Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void update(User user){
        String sql = "update t_user set username = ?,password = ? where id = ?";
        Object[] params = {user.getUsername(),user.getPassword(),user.getId()};
        jdbcTemplate.update(sql,params);
    }

    public List<User> findAll(){
        String sql = "select * from t_user";
                                        //封装对象的类型
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(User.class));
    }
}
