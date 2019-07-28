package mappers;

import entity.Role;

import java.util.List;

public interface RoleMapper {
    int insertRole(Role role);
    int deleteRole(Integer id);
    int updateRole(Role role);
    Role getRole(Integer id);
    //模糊查询
    List<Role> findRoles(String roleName);
}
