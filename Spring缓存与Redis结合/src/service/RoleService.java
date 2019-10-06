package service;

import pojo.Role;

import java.util.List;

public interface RoleService {

    Role getRole(Integer id);

    int deleteRole(Integer id);

    Role insertRole(Role role);

    Role updateRole(Role role);

    List<Role> findRoles(String roleName, String note);
}
