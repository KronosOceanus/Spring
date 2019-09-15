package controller;

import entity.Role;
import excel.ExcelExportService;
import excel.ExcelView;
import mappers.RoleMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import entity.PageParams;
import entity.RoleParams;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private RoleMapper roleMapper;

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public ModelAndView export(){
        ModelAndView mv = new ModelAndView();
        ExcelView ev = new ExcelView(exportService());
        ev.setFileName("所有文件.xlsx");
        RoleParams roleParams = new RoleParams();
        PageParams page = new PageParams();
        page.setStart(0);
        page.setLimit(10000);
        roleParams.setPageParams(page);
        List<Role> roleList = roleMapper.findRoles(roleParams);
        mv.addObject("roleList", roleList);
        mv.setView(ev);
        return mv;
    }

    //自定义导出接口
    @SuppressWarnings("unchecked")
    private ExcelExportService exportService(){
        return (Map<String, Object> model, Workbook workbook) -> {
            List<Role> roleList = (List<Role>) model.get("roleList");
            Sheet sheet = workbook.createSheet("所有角色");
            Row title = sheet.createRow(0);
            title.createCell(0).setCellValue("编号");
            title.createCell(1).setCellValue("名称");
            title.createCell(2).setCellValue("备注");
            for(int i=0;i<roleList.size();i++){
                Role role = roleList.get(i);
                int rowIdx = i + 1;
                Row row = sheet.createRow(rowIdx);
                row.createCell(0).setCellValue(role.getId());
                row.createCell(1).setCellValue(role.getRoleName());
                row.createCell(2).setCellValue(role.getNote());
            }
        };
    }
}
