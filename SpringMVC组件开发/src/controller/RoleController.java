package controller;

import entity.Role;
import mappers.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    //保存数据模型到 ModelAndView

    @RequestMapping(value = "/getRoleByModelMap", method = RequestMethod.GET)
    public ModelAndView getRoleByModelMap(@RequestParam("id") int id, ModelMap modelMap){
        Role role = roleMapper.getRole(id);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("roleDetails");
        modelMap.addAttribute("role", role); //自动绑定到 ModelAndView
        return mv;
    }

    @RequestMapping(value = "/getRoleByModel", method = RequestMethod.GET)
    public ModelAndView getRoleByModel(@RequestParam("id") int id, Model model){
        Role role = roleMapper.getRole(id);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("roleDetails");
        model.addAttribute("role", role); //自动绑定到 ModelAndView
        return mv;
    }

    @RequestMapping(value = "/getRoleByMv", method = RequestMethod.GET)
    public ModelAndView getRoleByModelMap(@RequestParam("id") int id, ModelAndView mv){
        Role role = roleMapper.getRole(id);
        mv.setViewName("roleDetails");
        mv.addObject("role", role);
        return mv;
    }

}
