package mappers;

import entity.Role;
import org.springframework.stereotype.Repository;
import pojo.RoleParams;

import java.util.List;

@Repository
public interface RoleMapper {

    Role getRole(int id);
    List<Role> findRoles(RoleParams roleParams);
    int deleteRoles(List<Integer> idList);
    int insertRoles(List<Role> roleList);
    int insertRole(Role role);
}
