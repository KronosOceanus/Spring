package mappers2;

import entity2.User2;

public interface UserMapper2 {

    User2 getUser(int id);

    User2 findUserByRoleId(int roleId);
}
