package mappers;

import entity.Role;
import org.springframework.stereotype.Repository;

@Repository //指定扫描 mapper
            //会被 MapperScannerConfigurer 扫描到并封装成 bean，名称为 roleMapper
public interface RoleMapper {

    int insertRole(Role role);
}
