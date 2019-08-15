package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

//标明控制器
@Controller("myController")
//标明在当前请求的 URI 在 /my 下才有该控制器响应（包名）
@RequestMapping("/my")
public class MyController {

    //标明 URL 是 /index 的时候该方法才请求（页面名）
    //对应 URL（localhost:8080/my/index.do）
    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView();
        //视图逻辑名称
        mv.setViewName("index");
        return mv;
    }
}
