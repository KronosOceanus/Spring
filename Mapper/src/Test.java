import entity.Page;
import entity.Role;
import mappers.KeyMapper;
import mappers.PageMapper;
import mappers.SqlMapper;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import utils.SqlSessionUtils;

public class Test {

    //多参数分页测试
    private void test01(){
        try(
                SqlSession sqlSession = SqlSessionUtils.openSqlSession();
        ){
            Page page = new Page(2,5);
            Role role = new Role("ac","no");
            PageMapper mapper = sqlSession.getMapper(PageMapper.class);
            for (Role r : mapper.findByMix(role, page)){
                System.out.println(r);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //分页参数 RowBounds 测试
    private void test02(){
        try(
                SqlSession sqlSession = SqlSessionUtils.openSqlSession();
        ){
            PageMapper mapper = sqlSession.getMapper(PageMapper.class);
            for (Role r : mapper.findByRowBounds(
                    "ac","no",new RowBounds(2,5))){
                System.out.println(r);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //主键测试
    private void test03(){
        try(
                SqlSession sqlSession = SqlSessionUtils.openSqlSession();
        ){
            Role role = new Role("fk","is fk note!");
            KeyMapper keyMapper = sqlSession.getMapper(KeyMapper.class);
            keyMapper.insertRole2(role);
            System.out.println(role.getId());
            sqlSession.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // sql 引用测试
    private void test04(){
        try(
                SqlSession sqlSession = SqlSessionUtils.openSqlSession();
        ){
            SqlMapper sqlMapper = sqlSession.getMapper(SqlMapper.class);
            Role role = sqlMapper.getRole2(34);
            System.out.println(role);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Test t = new Test();
        t.test04();
    }
}
