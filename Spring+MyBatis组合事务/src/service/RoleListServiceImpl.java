package service;

import entity.Role;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("roleListService")
public class RoleListServiceImpl implements RoleListService {

    Logger logger = Logger.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleService roleService = null;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public int insertRoleList(List<Role> roleList) {
        int count = 0;
        for (Role role : roleList){
            try{
                count += roleService.insertRole(role);
            }catch (Exception e){
                logger.info(e);
                //自行抛出异常，让 Spring 继续事务管理
                throw new RuntimeException(e);
            }
        }
        return count;
    }
}
