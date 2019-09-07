package controller;

import entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import service.RoleService;

@Controller("roleController")
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // URL（http://localhost:8080/SpringMVC_war_exploded/role/getRole.do?id=15）
    @RequestMapping("/getRole")
    public ModelAndView getRole(@RequestParam("id") int id){
        Role role = roleService.getRole(id);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("roleDetails");
        //添加对象到视图
        mv.addObject(role);
        System.out.println(role);
        return mv;
    }
    // URL（http://localhost:8080/SpringMVC_war_exploded/role/getRoleToJson.do?id=15）
    @RequestMapping("/getRoleToJson")
    public ModelAndView getRoleToJson(@RequestParam("id") int id){
        Role role = roleService.getRole(id);
        ModelAndView mv = new ModelAndView();
        //添加对象到视图
        mv.addObject(role);
        //设置视图类型
        mv.setView(new MappingJackson2JsonView());
        return mv;
    }



    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
}
