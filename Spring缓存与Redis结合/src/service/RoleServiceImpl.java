package service;

import mappers.RoleMapper;
import org.hamcrest.core.Is;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pojo.Role;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override                              //读写提交                  //沿用上一个传播行为
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
                          //引用缓存       //得到参数的 key 并参数保存到缓存
    @Cacheable(value = "redisCacheManager", key = "'redis_role_'+#id")  //缓存中有就读取缓存，否则执行方法
    public Role getRole(Integer id) {
        return roleMapper.getRole(id);
    }

    @Override
    public int deleteRole(Integer id) {
        return 0;
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @CachePut(value = "redisCacheManager", key = "'redis_role_'+#result.id")  //执行方法，更新缓存
    public Role insertRole(Role role) {
        roleMapper.insertRole(role);
        //自动主键回填
        return role;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @CachePut(value = "redisCacheManager", key = "'redis_role_'+#role.id")  //执行方法，更新缓存
    public Role updateRole(Role role) {
        roleMapper.updateRole(role);
        return role;
    }

    @Override
    public List<Role> findRoles(String roleName, String note) {
        return null;
    }
}
