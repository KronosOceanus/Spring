package controller;

import entity.FormatPojo;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Date;

@Controller
@RequestMapping("/formatter")
public class FormatController {

    @RequestMapping("/format")
    public ModelAndView format(
            //格式化
            @RequestParam("date1") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Date date,
            @RequestParam("amount1") @NumberFormat(pattern = "#,###.##") Double amount
            ){
        ModelAndView mv = new ModelAndView();
        mv.addObject("date", date);
        mv.addObject("amount", amount);
        mv.setView(new MappingJackson2JsonView());
        return mv;
    }

    @RequestMapping("/formatPojo")
    public ModelAndView formatPojo(FormatPojo pojo){
        ModelAndView mv = new ModelAndView();
        mv.setView(new MappingJackson2JsonView());
        return mv;
    }
}
