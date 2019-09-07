package controller;

import entity.Role;
import mappers.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Controller
@RequestMapping("/attribute")
@SessionAttributes(names = {"id"}, types = {Role.class})
public class AttributeController {

    @Autowired
    private RoleMapper roleMapper;

    //获取 request 中的参数
    @RequestMapping("/requestAttribute")
    public ModelAndView reqAttr(@RequestAttribute("id") int id){
        ModelAndView mv = new ModelAndView();
        Role role = roleMapper.getRole(id);
        mv.addObject("role", role);
        mv.setView(new MappingJackson2JsonView());
        return mv;
    }

    //访问 http://localhost:8080/SpringMVC_war_exploded/attribute/sessionAttributes.form?id=1 测试
    @RequestMapping("/sessionAttributes")
    public ModelAndView sessionAttrs(int id){
        ModelAndView mv = new ModelAndView();
        Role role = roleMapper.getRole(id);
        //添加 key - value 符合 @SessionAttributes 定义，自动储存到 session 中（解决 servlet API 入侵）
        mv.addObject("role", role);
        mv.addObject("id", id);
        //用视图验证
        mv.setViewName("sessionAttribute");
        return mv;
    }

    //获取 session 中参数
    @RequestMapping("/sessionAttribute")
    public ModelAndView sessionAttr(@SessionAttribute("id") int id){
        ModelAndView mv = new ModelAndView();
        Role role = roleMapper.getRole(id);
        mv.addObject("role", role);
        mv.setView(new MappingJackson2JsonView());
        return mv;
    }

    //从 cookie 和 http header 中获取请求信息
    @RequestMapping("getHeaderAndCookie")
    public String testHeaderAndCookie(
            @RequestHeader(value = "User-Agent", required = true, defaultValue = "attribute") String userAgent,
            @CookieValue(value = "JSESSIONID", required = true, defaultValue = "MyJsessionId") String jsessionId
    ){
        System.out.println("User-Agent => " + userAgent);
        System.out.println("JSESSIONID => " + jsessionId);
        return "index";
    }
}
