package dao;

import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
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

    public User findUserByUsername(String username){
        String sql = "select * from t_user where username = " + username;
        return jdbcTemplate.queryForObject(sql, (resultSet, i) -> {
            User result = new User();
            result.setId(resultSet.getInt("id"));
            result.setUsername(resultSet.getString("username"));
            result.setPassword(resultSet.getString("password"));
            return result;
        });
    }

    public int deleteUser(int id){
        String sql = "delete from t_user where id = ?";
        return jdbcTemplate.update(sql, id);
    }


    //执行多条 SQL
    // ConnectionCallback 接口回调
    public User getUserByConnectionCallback(int id){
        User user = null;
        user = jdbcTemplate.execute((ConnectionCallback<User>) connection -> {
            User result = null;
            String sql = "select * from t_user where id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = new User();
                result.setId(rs.getInt("id"));
                result.setUsername(rs.getString("username"));
                result.setPassword(rs.getString("password"));
            }
            return result;
        });
        return user;
    }

    // StatementCallback 接口回调
    public User getUserByStatementCallback(int id){
        User user = null;
        user = jdbcTemplate.execute((StatementCallback<User>)  stmt -> {
            User result = null;
            String sql = "select * from t_user where id = " + id;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                result = new User();
                result.setId(rs.getInt("id"));
                result.setUsername(rs.getString("username"));
                result.setPassword(rs.getString("password"));
            }
            return result;
        });
        return user;
    }
}
