package controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import validator.Transaction;
import validator.TransactionValidator;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/validate")
public class ValidateController {
/**
    //绑定 Spring 内置 Validator（与 JSR 303 不能同时使用 / 优先 Spring）
    @InitBinder //初始化绑定者
    public void initBinder(DataBinder binder){
        //加入验证器
        binder.setValidator(new TransactionValidator());
    }
*/

    @RequestMapping("/annotation")        //标明该 bean 会被用于检验， 检验后错误
    public ModelAndView annotationValidate(@Valid Transaction trans, Errors errors){
        //有错误信息就输出（到后台）
        if (errors.hasErrors()){
            List<FieldError> errorList = errors.getFieldErrors();
            for (FieldError error : errorList){
                System.out.println("field: " + error.getField() + "\t" +
                        "msg: " + error.getDefaultMessage());
            }
        }
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        return mv;
    }
}
