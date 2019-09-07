package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/file")
public class FileController {

    @RequestMapping("/upload")
    public ModelAndView upload(MultipartFile file){
        ModelAndView mv = new ModelAndView();
        mv.setView(new MappingJackson2JsonView());
        //获取原始文件名
        String fileName = file.getOriginalFilename();
        file.getContentType();
        //客户端目标文件
        File dest = new File(fileName);
        try{
            file.transferTo(dest);
            mv.addObject("success", true);
            mv.addObject("msg", "上传文件成功！");
        } catch (IOException e) {
            mv.addObject("success", false);
            mv.addObject("msg", "上传文件失败！");
            e.printStackTrace();
        }
        return mv;
    }
}
