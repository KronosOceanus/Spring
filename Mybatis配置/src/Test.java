import entity.Role;
import entity.TestFile;
import entity.User;
import mappers.FileMapper;
import mappers.RoleMapper;
import mappers.UserMapper;
import org.apache.ibatis.session.SqlSession;
import utils.SqlSessionFactoryUtils;

public class Test {

    //自定义 TypeHandler 测试
    private void test01(){
        try(
                SqlSession sqlSession = SqlSessionFactoryUtils.openSqlSession();
                ){
            Role role = new Role(18,"jack","is jacks note!");

            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
            roleMapper.insertRole(role);
            sqlSession.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //枚举处理器测试 / 对象工厂测试
    private void test02(){
        try(
                SqlSession sqlSession = SqlSessionFactoryUtils.openSqlSession();
        ){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            System.out.println(userMapper.getUser(1));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //文件处理器测试
    private void test03(){
        try(
                SqlSession sqlSession = SqlSessionFactoryUtils.openSqlSession();
        ){
            TestFile file = new TestFile("jdbc.properties");
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            byte[] content = fileMapper.getFile(1).getContent();
            String contentString = new String(content,0,content.length);
            System.out.println(contentString);
            sqlSession.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Test t = new Test();
        t.test02();
    }
}
