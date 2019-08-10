package mappers;

import entity.Role;

public interface KeyMapper {

    void insertRole1(Role role);

    //自定义主键
    void insertRole2(Role role);
}
