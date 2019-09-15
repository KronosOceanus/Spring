package controller;

import entity.Role;
import mappers.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    //测试 url：http://localhost:8080//SpringMVC_war_exploded/role/getRole?id=1
    @RequestMapping("/getRole")
    @ResponseBody //使 MVC 把结果转换为 json 类型响应，进而找到转换器
    public Role getRole(int id){
        return roleMapper.getRole(id);
    }

    //测试 url：http://localhost:8080//SpringMVC_war_exploded/role/updateRole.form?role=1-mvc-mvp!
    @RequestMapping("/updateRole")
    @ResponseBody
    public Map<String, Object> updateRole(Role role){
        Map<String ,Object> map = new HashMap<>();
        boolean updateFlag = (roleMapper.updateRole(role) == 1);
        map.put("success", updateFlag);
        if (updateFlag){
            map.put("msg", "更新成功");
        }else {
            map.put("msg", "更新失败");
        }
        return map;
    }
}
