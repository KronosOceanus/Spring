package type_handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 使用包扫描（但是包扫描无法注册对应类型）
 * 通过注解注册对应类型
 */
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StringTypeHandler implements TypeHandler<String> {

    //日志打印，（包名 org.apache.log4j.Logger）
    Logger logger = Logger.getLogger(StringTypeHandler.class);

    //？？？？？？？
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
        logger.info("设置 string 参数【" + s + "】");
        preparedStatement.setString(i,s);
    }


    /**
     * 查询时触发
     */
    @Override
    public String getResult(ResultSet resultSet, String s) throws SQLException {
        String result = resultSet.getString(s);
        logger.info("读取 string 参数1【" + result + "】");
        return result;
    }
    @Override
    public String getResult(ResultSet resultSet, int i) throws SQLException {
        String result = resultSet.getString(i);
        logger.info("读取 string 参数2【" + result + "】");
        return result;
    }
    @Override
    public String getResult(CallableStatement callableStatement, int i) throws SQLException {
        String result = callableStatement.getString(i);
        logger.info("读取 string 参数3【" + result + "】");
        return result;
    }
}
