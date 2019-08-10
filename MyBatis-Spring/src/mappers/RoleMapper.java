package mappers;

import entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

//注解定义 Mapper
@Repository                     //扩展名定义 Mapper
public interface RoleMapper extends BaseMapper{
    int insertRole(Role role);
    int deleteRole(Integer id);
    int updateRole(Role role);
    Role getRole(Integer id);
    //模糊查询
    List<Role> findRoles(String roleName);
}
