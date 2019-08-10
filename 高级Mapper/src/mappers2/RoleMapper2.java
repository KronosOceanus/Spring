package mappers2;

import entity2.Role2;

public interface RoleMapper2 {

    Role2 getRole(int id);

    Role2 findRoleByUserId(int userId);
}
