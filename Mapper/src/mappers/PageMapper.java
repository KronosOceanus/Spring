package mappers;

import entity.Page;
import entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface PageMapper {

    //传递多个参数（多个 bean）
    List<Role> findByMix(@Param("params") Role role,
                         @Param("page") Page page);

    //分页参数
    List<Role> findByRowBounds(@Param("roleName") String roleName,
                         @Param("note") String note,
                         RowBounds rowBounds);
}
