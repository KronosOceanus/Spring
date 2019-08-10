package entity;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

//需要在配置文件中配置 DataSource 的 bean
public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {

    @Override
    public void out(String outer, Integer money) {
        this.getJdbcTemplate().update("update account set money = money - ? where username = ?",
                money, outer);
    }

    @Override
    public void in(String inner, Integer money) {
        this.getJdbcTemplate().update("update account set money = money + ? where username = ?",
                money, inner);
    }
}
