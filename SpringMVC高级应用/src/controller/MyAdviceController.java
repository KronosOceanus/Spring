package controller;

import entity.Role;
import exception.RoleException;
import mappers.RoleMapper;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/advice")
public class MyAdviceController {


    @Autowired
    private RoleMapper roleMapper;

    @RequestMapping("/test")
    @ResponseBody
    public Map<String, Object> testAdvice(Date date1, @NumberFormat(pattern = "##,###.00")BigDecimal amount1, Model model){
        Map<String, Object> map = new HashMap<>();
        map.put("project_name", model.asMap().get("projectName"));
        map.put("date", DateUtils.format(date1, "yyyy-MM-dd"));
        map.put("amount", amount1);
        return map;
    }

    @RequestMapping("/exception")
    public void exception(){
        throw new RuntimeException("测试异常跳转！");
    }

    //测试url：http://localhost:8080/SpringMVC_war_exploded/advice/notFound.form?id=2
    @RequestMapping("/notFound")
    @ResponseBody
    public Role noteFound(int id){
        Role role = roleMapper.getRole(id);
        if (role == null){
            throw new RoleException();
        }
        return role;
    }
}
