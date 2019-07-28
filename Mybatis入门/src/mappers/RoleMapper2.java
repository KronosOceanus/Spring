package mappers;

import entity.Role;
import org.apache.ibatis.annotations.Select;

/**
 * 注解实现映射器
 */
public interface RoleMapper2 {

    @Select("select id, role_name as roleName, note from t_role where id = #{id}")
    Role getRole(Integer id);
}
