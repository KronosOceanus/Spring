import entity.Employee;
import entity2.Role2;
import entity2.User2;
import mappers.EmployeeMapper;
import mappers2.RoleMapper2;
import mappers2.UserMapper2;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import utils.SqlSessionUtils;

public class Test {

    //级联测试
    private void test01(){
        try(
                SqlSession sqlSession = SqlSessionUtils.openSqlSession();
                ){
            Logger logger = Logger.getLogger(Test.class);
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.getEmployee(1);
            logger.info(employee.getBirthday());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //多对多级联测试
    private void test02(){
        try(
                SqlSession sqlSession = SqlSessionUtils.openSqlSession();
                ){
            Logger logger = Logger.getLogger(Test.class);
            RoleMapper2 roleMapper2 = sqlSession.getMapper(RoleMapper2.class);
            Role2 role2 = roleMapper2.getRole(1);
            UserMapper2 userMapper2 = sqlSession.getMapper(UserMapper2.class);
            User2 user2 = userMapper2.getUser(1);
            logger.info(role2.getUser2s());
            logger.info(user2.getRole2s());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //二级缓存测试（观察 SQL 执行次数）
    private void test03(){
        try(
                SqlSession sqlSession = SqlSessionUtils.openSqlSession();
                SqlSession sqlSession1 = SqlSessionUtils.openSqlSession();
        ){
            Logger logger = Logger.getLogger(Test.class);
            RoleMapper2 roleMapper2 = sqlSession.getMapper(RoleMapper2.class);
            RoleMapper2 roleMapper21 = sqlSession1.getMapper(RoleMapper2.class);
            Role2 role2 = roleMapper2.getRole(1);
            //提交才能到二级缓存（分开提交）
            sqlSession.commit();
            Role2 role21 = roleMapper21.getRole(1);
            sqlSession1.commit();
            logger.info(role2.getRoleName() + " " + role21.getRoleName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Test t = new Test();
        t.test03();
    }
}
