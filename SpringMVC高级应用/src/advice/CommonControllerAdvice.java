package advice;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice(basePackages = {"controller"})
public class CommonControllerAdvice {

    //定义 http 参数对应处理规则
    @InitBinder
    public void initBinder(WebDataBinder binder){
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),true);
        binder.registerCustomEditor(Date.class, editor);
    }

    //绑定数据
    @ModelAttribute
    public void populateModel(Model model){
        model.addAttribute("projectName", "SpringMVC高级应用");
    }

    //异常通知
    @ExceptionHandler(Exception.class)
    public String exception(){
        return "exception";
    }
}
