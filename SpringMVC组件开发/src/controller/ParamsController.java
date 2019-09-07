package controller;

import entity.Role;
import mappers.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import pojo.RoleParams;

import java.util.List;

@Controller
@RequestMapping("/params")
public class ParamsController {

    @Autowired
    private RoleMapper roleMapper;

    @RequestMapping("/commonParams")
    public ModelAndView commonParams(String roleName, String note){
        System.out.println("roleName => " + roleName);
        System.out.println("note =>" + note);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        return mv;
    }

    @RequestMapping("/commonParamPojo")
    public ModelAndView commonParamPojo(RoleParams roleParams){
        System.out.println("roleName => " + roleParams.getRoleName());
        System.out.println("note =>" + roleParams.getNote());
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        return mv;
    }

    // URL 内参数
    @RequestMapping("/getRole/{id}")
    public ModelAndView pathVariable(@PathVariable("id") int id){
        Role role = roleMapper.getRole(id);
        System.out.println("roleName => " + role.getRoleName());
        System.out.println("note =>" + role.getNote());
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        return mv;
    }

    //通过请求体发送参数（在 post 请求中）
    @RequestMapping("/findRoles")
    public ModelAndView findRoles(@RequestBody RoleParams roleParams){
        List<Role> roleList = roleMapper.findRoles(roleParams);
        System.out.println(roleList);
        ModelAndView mv = new ModelAndView();
        mv.addObject(roleList);
        mv.setView(new MappingJackson2JsonView());
        return mv;
    }

    //传递数组
    @RequestMapping("/deleteRoles")
    public ModelAndView deleteRoles(@RequestBody List<Integer> idList){
        ModelAndView mv = new ModelAndView();
        System.out.println(idList);
        int total = roleMapper.deleteRoles(idList);
        mv.addObject("total",total);
        mv.setView(new MappingJackson2JsonView());
        return mv;
    }

    @RequestMapping("/insertRoles")
    public ModelAndView insertRoles(@RequestBody List<Role> roleList){
        ModelAndView mv = new ModelAndView();
        System.out.println(roleList);
        int total = roleMapper.insertRoles(roleList);
        mv.addObject("total", total);
        mv.setView(new MappingJackson2JsonView());
        return mv;
    }

    //序列化表单
    @RequestMapping("/commonParamPojo2")
    public ModelAndView serializeForm(String roleName, String note){
        System.out.println("roleName => " + roleName);
        System.out.println("note => " + note);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        return mv;
    }

    //展示 json 视图
    @RequestMapping("/showRoleJsonInfo")
    public ModelAndView showRoleJsonInfo(int id, String roleName, String note){
        ModelAndView mv = new ModelAndView();
        mv.setView(new MappingJackson2JsonView());
        mv.addObject("id", id);
        mv.addObject("roleName", roleName);
        mv.addObject("note", note);
        return mv;
    }

    //返回字符串的视图重定向（主键回填，利用模型，最后重定向到展示事务）
    @RequestMapping("/addRole")
    public String addRole(Model model, String roleName, String note){
        Role role = new Role();
        role.setRoleName(roleName);
        role.setNote(note);
        //主键回填
        roleMapper.insertRole(role);
        model.addAttribute("roleName", roleName);
        model.addAttribute("note", note);
        model.addAttribute("id", role.getId());
        //重定向
        return "redirect:./showRoleJsonInfo.form";
    }

    //返回视图和模型的重定向
    @RequestMapping("/addRole2")
    public ModelAndView addRole2(ModelAndView mv, String roleName, String note){
        Role role = new Role();
        role.setRoleName(roleName);
        role.setNote(note);
        //主键回填
        roleMapper.insertRole(role);
        mv.addObject("roleName", roleName);
        mv.addObject("note", note);
        mv.addObject("id", role.getId());
        mv.setViewName("redirect:./showRoleJsonInfo.form");
        return mv;
    }

    // pojo 转换为 json 视图
    //但是重定向过程中不能有效传递对象，因为重定向参数是以字符串的形式传递的
    @RequestMapping("/showRoleJsonInfo2")
    public ModelAndView showRoleJsonInfo2(Role role){
        ModelAndView mv = new ModelAndView();
        mv.setView(new MappingJackson2JsonView());
        mv.addObject("role", role);
        return mv;
    }

    //解决重定向传递对象问题
    @RequestMapping("/addRole3")
    public String addRole3(RedirectAttributes ra, Role role){
        roleMapper.insertRole(role);
        //将对象绑定到 session 中，重定向后清除
        ra.addFlashAttribute("role", role);
        return "redirect:./showRoleJsonInfo2.form";
    }

}
