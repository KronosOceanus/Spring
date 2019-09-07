package mappers;

import entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMapper {

    Role getRole(int id);
}
