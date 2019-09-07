package excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 视图类
 */
public class ExcelView extends AbstractXlsView {

    //导出需要一个文件名
    private String fileName = null;
    //导出视图的自定义接口
    private ExcelExportService excelExportService = null;


    public ExcelView(ExcelExportService excelExportService){
        this.excelExportService = excelExportService;
    }
    public ExcelView(String fileName, ExcelExportService excelExportService){
        this.fileName = fileName;
        this.excelExportService = excelExportService;
    }

    //主要任务是创建一个 WorkBook
    @Override
    protected void buildExcelDocument(Map<String, Object> map, Workbook workbook, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        if (excelExportService == null){
            throw new RuntimeException("导出服务接口不能为 null ！");
        }
        //Spring 框架的字符串工具
        if (!StringUtils.isEmpty(fileName)){
            String reqCharset = httpServletRequest.getCharacterEncoding();
            reqCharset = reqCharset == null ? "UTF-8" : reqCharset;
            fileName = new String(fileName.getBytes(reqCharset),"ISO8859-1");
            //设置文件名称
            httpServletResponse.setHeader("Content-disposition", "attachment:filename=" + fileName);
        }
        //接口回调方法
        excelExportService.makeWorkBook(map, workbook);
    }







    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ExcelExportService getExcelExportService() {
        return excelExportService;
    }

    public void setExcelExportService(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }
}
