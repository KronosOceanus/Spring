package controller;

import entity.Role;
import mappers.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/roleAdvice")
public class RoleAdviceController {

    @Autowired
    private RoleMapper roleMapper;

    //进入控制器方法前运行（只对本控制器有效）
    @ModelAttribute("role")     // key = "role", value = 返回值
    public Role initRole(@RequestParam(value = "id", required = false) int id){
        if (id < 1){
            return null;
        }
        return roleMapper.getRole(id);
    }

    //测试url：http://localhost:8080//SpringMVC_war_exploded/roleAdvice/getRoleFromModelAttribute.form?id=1
    //取出数据
    @RequestMapping("/getRoleFromModelAttribute")
    @ResponseBody
    public Role getRoleFromModelAttribute(@ModelAttribute("role") Role role){
        return role;
    }

}
