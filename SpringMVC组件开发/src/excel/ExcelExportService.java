package excel;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

/**
 * Excel 视图自定义生成规则
 */
public interface ExcelExportService {
                                                        //需要 POI 包
    public void makeWorkBook(Map<String, Object> model, Workbook workbook);
}