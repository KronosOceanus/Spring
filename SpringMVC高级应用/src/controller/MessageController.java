package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/message")
public class MessageController {

    //测试url：http://localhost:8080/SpringMVC_war_exploded/message/msgpage.form?language=zh_CN
    //测试url：http://localhost:8080/SpringMVC_war_exploded/message/msgpage.form?language=en_US
    //国际化
    @RequestMapping("/msgpage")
    public String page(Model model){
        return "msgpage";
    }
}
