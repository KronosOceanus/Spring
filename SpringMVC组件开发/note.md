[TOC]

# Spring MVC 组件开发
## 控制器获取参数
#### 直接使用参数名
```java
    public ModelAndView commonParams(String roleName, String note){}
```
#### 使用 pojo 封装参数
```java
    public ModelAndView commonParamPojo(RoleParams roleParams){
        System.out.println("roleName => " + roleParams.getRoleName());
        System.out.println("note =>" + roleParams.getNote());
    }
```
* 或者使用 @RequestParam("id") 获取请求中 name 为 id 的参数

#### URL 传递参数
```java
    @RequestMapping("/getRole/{id}")
    public ModelAndView pathVariable(@PathVariable("id") int id)
```

#### 传递 json 参数
将 json 数据封装到请求体中，在控制器中使用 @RequestBody 注解取出，例：
* 客户端传递 json 数据（jQuery）
```jsp
    function find() {
        var data = {
            "roleName":"role",
            "note":"note",
            "pageParams":{
                "start":1,
                "limit":20
            }
        };
        $.post({
            url:"../params/findRoles.form",
            contentType:"application/json",
            data:JSON.stringify(data),
            success: function (result) {
            }
        });
    }
```

* 控制器接收参数
```java
    @RequestMapping("/findRoles")
    public ModelAndView findRoles(@RequestBody RoleParams roleParams)
```
#### 接收列表数据
* json 数组
```jsp
    var idList = [1,2,3];
```
* 接收参数同上，参数类型为 List<Integer>

#### 表单序列化
有时候隐藏的表单需要一定的计算，这时就需要将表单序列化（转化为字符串传递给后台）
* 提交序列化表单
```html
    <!-- 提交函数 -->
    <script type="text/javascript">
        function c(){
            //根据 id 引用，将表单序列化
            var str = $("form").serialize();
            $.post({
                url:"../params/commonParamPojo2.form",
                data:str,
                success:function (result) {
                }
            });
        }
    </script>
    
    <!-- 提交按键 -->
    <tr>
        <td align="right"><input type="button" value="提交" onclick="c()"/></td>
    </tr>
```
* 获取参数
```java
    @RequestMapping("/commonParamPojo2")
    public ModelAndView serializeForm(String roleName, String note)
```

## 重定向
从前台传来部分数据，再通过数据库查询得到全部数据，绑定模型到视图，最后返回视图 / 字符串
#### 参数转化为 json 视图
* 展示 json 数据的控制器
```java
    //展示 json 视图
    @RequestMapping("/showRoleJsonInfo")
    public ModelAndView showRoleJsonInfo(int id, String roleName, String note){
        ModelAndView mv = new ModelAndView();
        mv.setView(new MappingJackson2JsonView());
        mv.addObject("id", id);
        mv.addObject("roleName", roleName);
        mv.addObject("note", note);
        return mv;
    }
```
* 重定向到展示 json 视图的控制器（的控制器）
```java
    //返回字符串的视图重定向（主键回填，利用模型，最后重定向到展示事务）
    @RequestMapping("/addRole")
    public String addRole(Model model, String roleName, String note){
        Role role = new Role();
        role.setRoleName(roleName);
        role.setNote(note);
        //主键回填
        roleMapper.insertRole(role);
        model.addAttribute("roleName", roleName);
        model.addAttribute("note", note);
        model.addAttribute("id", role.getId());
        //重定向
        return "redirect:./showRoleJsonInfo.form";
    }
```
返回字符串相当于视图设置名称，再返回视图
#### pojo 直接转化为 json 视图
* 绑定 pojo 到 json 视图
```java
    mv.addObject("role", role);
```
* 重定向传递 pojo
```java
    //解决重定向传递对象问题
    @RequestMapping("/addRole3")
    public String addRole3(RedirectAttributes ra, Role role){
        roleMapper.insertRole(role);
        //将对象绑定到 session 中，重定向后清除
        ra.addFlashAttribute("role", role);
        return "redirect:./showRoleJsonInfo2.form";
    }
```
由于 URL 重定向过程中参数以字符串形式传递，Spring MVC 提供了一个方法 -- flash 属性，需要提供的数据模型
就是一个 RedirectAttributes

## 保存并获取属性参数
先设置 Attribute ，再重定向到控制器
#### @RequestAttribute / @SessionAttribute
在控制器中获取储存在 request / session 中的 Attribute 参数
#### @SessionAttributes
* 使用方法
```java
    //修饰控制器
    @SessionAttributes(names = {"id"}, types = {Role.class})
    public class AttributeController 
```
* 数据模型保存数据
```java
    mv.addObject("role", role);
    mv.addObject("id", id);
```
添加 key - value 符合 @SessionAttributes 定义，自动储存到 session 中（解决 servlet API 入侵）

#### @CookieValue / @ RequestHeader
```java
    //从 cookie 和 http header 中获取请求信息
    @RequestMapping("getHeaderAndCookie")
    public String testHeaderAndCookie(
            @RequestHeader(value = "User-Agent", required = true, defaultValue = "attribute") String userAgent,
            @CookieValue(value = "JSESSIONID", required = true, defaultValue = "MyJsessionId") String jsessionId
    ){
        System.out.println("User-Agent => " + userAgent);
        System.out.println("JSESSIONID => " + jsessionId);
        return "index";
    }
```

## 拦截器
必须实现 org.spiringframework.web.servlet.HandlerInterceptor 接口
#### HandlerInterceptor 接口源码
```java
    public interface HandlerInterceptor {
        //处理器执行前方法
        boolean preHandle(HttpServletRequest var1, HttpServletResponse var2, Object var3) throws Exception;
        //后方法
        void postHandle(HttpServletRequest var1, HttpServletResponse var2, Object var3, ModelAndView var4) throws Exception;
        //完成方法，渲染视图后执行
        void afterCompletion(HttpServletRequest var1, HttpServletResponse var2, Object var3, Exception var4) throws Exception;
    }
```
执行流程：前方法返回 true？处理器 -> 后方法 -> 视图解析和渲染 -> 完成方法 ：null

#### 自定义拦截器
* 继承 HandlerInterceptorAdapter，覆盖原有方法（可以覆盖部分）
* 配置拦截器（在 dispatcher-servlet.xml 中，或者其他 spring 配置）
```xml
    <!-- 配置拦截器 -->
    <mvc:interceptors>
        <mvc:interceptor>
            <!-- 多级路径（* 代表 1 级） -->
            <mvc:mapping path="/**"/>
            <bean class="interceptor.RoleInterceptor" />
        </mvc:interceptor>
    </mvc:interceptors>
```

## 验证表单
#### 导入 jar
* validate-api-FINAL.jar
* hibernate-validator.jar
* classmate.jar
* jboss-logging.jar

#### 验证实例
* 表单
```html
    <form action="../validate/annotation.form">
        <table>
            <tr>
                <td>产品编号：</td>
                <td><input name="productId" id="productId" /></td>
            </tr>
            <tr>
                <td>用户编号：</td>
                <td><input name="userId" id="userId"/></td>
            </tr>
            <tr>
                <td>交易日期：</td>
                <td><input name="date" id="date" /></td>
            </tr>
            <tr>
                <td>价格：</td>
                <td><input name="price" id="price" /></td>
            </tr>
            <tr>
                <td>数量：</td>
                <td><input name="quantity" id="quantity"/></td>
            </tr>
            <tr>
                <td>交易金额：</td>
                <td><input name="amount" id="amount"/></td>
            </tr>
            <tr>
                <td>用户邮件：</td>
                <td><input name="email" id="email" /></td>
            </tr>
            <tr>
                <td>备注：</td>
                <td><textarea id="note" name="note" cols="20" rows="5"></textarea></td>
            </tr>
            <tr><td colspan="2" align="right"><input value="提交" type="submit" /></td></tr>
        </table>
    </form>
```
* 用于验证的 bean
```java
    public class Transaction {
    
        @NotNull
        private int productId;
    
        @NotNull
        private int userId;
    
        @Future //只能是将来的日期
        @DateTimeFormat(pattern = "yyyy-MM-dd") //日期格式
        @NotNull
        private Date date;
    
        @NotNull
        @DecimalMin(value = "0.1") //最小值 0.1 元
        private Double price;
    
        @Min(1)
        @Max(100)
        @NotNull
        private Integer quantity;
    
        @NotNull
        @DecimalMax("50000.00") //最大金额
        @DecimalMin("1.00")
        private Double amount;
    
        @Pattern(regexp = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@" +
                "([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][a-zA-Z]{2,3}" +
                "([\\.][A-Za-z]{2})?$",
                message = "不符合邮件格式")
        private String email;
    
        @Size(min = 0, max = 256) // 0-256 个字符
        private String note;
    
        /***** setter/getter *****/
    }
```
* 在控制器中应用
```java
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
```

#### 使用 MVC 自带验证器
自定义验证器需要实现 org.springframework.validation.Validator 接口
#### Validator 接口源码
```java
    public interface Validator {
        //判断当前验证器是否能用于检测该表单
        boolean supports(Class<?> var1);
        //检测
        void validate(Object var1, Errors var2);
    }
```
#### 验证器实例
* 验证器
```java
    public class TransactionValidator implements Validator {
    
        //如果表单与 Transaction 类匹配，则校验
        @Override
        public boolean supports(Class<?> aClass) {
            return Transaction.class.equals(aClass);
        }
    
        //具体校验逻辑
        @Override
        public void validate(Object o, Errors errors) {
            //先获取 bean 对象，再根据内容校验
            Transaction trans = (Transaction) o;
            double dis = trans.getAmount() - (trans.getPrice() * trans.getQuantity());
            if (Math.abs(dis) > 0.01){
                //加入错误信息
                errors.rejectValue("amount", null, "交易金额和购买数量与价格不匹配！");
            }
        }
    }
```
* 在控制器中绑定验证器
```java
    //绑定 Spring 内置 Validator（与 JSR 303 不能同时使用 / 优先 Spring）
    @InitBinder //初始化绑定者
    public void initBinder(DataBinder binder){
        //加入验证器
        binder.setValidator(new TransactionValidator());
    }
```

## 数据模型
* 通过不同的数据模型，绑定数据到视图
```java
    @RequestMapping(value = "/getRoleByModelMap", method = RequestMethod.GET)
    public ModelAndView getRoleByModelMap(@RequestParam("id") int id, ModelMap modelMap){
        Role role = roleMapper.getRole(id);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("roleDetails");
        modelMap.addAttribute("role", role); //自动绑定到 ModelAndView
        return mv;
    }

    @RequestMapping(value = "/getRoleByModel", method = RequestMethod.GET)
    public ModelAndView getRoleByModel(@RequestParam("id") int id, Model model){
        Role role = roleMapper.getRole(id);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("roleDetails");
        model.addAttribute("role", role); //自动绑定到 ModelAndView
        return mv;
    }

    @RequestMapping(value = "/getRoleByMv", method = RequestMethod.GET)
    public ModelAndView getRoleByModelMap(@RequestParam("id") int id, ModelAndView mv){
        Role role = roleMapper.getRole(id);
        mv.setViewName("roleDetails");
        mv.addObject("role", role);
        return mv;
    }
```

## 视图和视图解析器
自定义视图实现接口 org.springframework.web.serlvet.View 接口，视图解析器接口是同包下的 ViewResolver
#### View 接口源码
```java
    public interface View {
        String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";
        String PATH_VARIABLES = View.class.getName() + ".pathVariables";
        String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";
        //得到响应类型（如 HTML,JSON,PDF 等）
        String getContentType();
        //渲染视图，var1 是数据模型
        void render(Map<String, ?> var1, HttpServletRequest var2, HttpServletResponse var3) throws Exception;
    }
```
视图有很多种，具体视图类型可以查阅
#### ViewResolver接口源码
```java
    public interface ViewResolver {
        // Locale 是用于国际化的参数
        View resolveViewName(String var1, Locale var2) throws Exception;
    }
```
#### 使用 Excel 视图实例
* 导入 POI.jar
* 接口继承类 org.springframework.web.servlet.view.document.AbstractXlsView，其中主要方法
```java
    //主要任务是创建一个 WorkBook，要用到 POI 的 API
    protected abstract void buildExcelDocument(Map<String, Object> var1, Workbook var2, 
        HttpServletRequest var3, HttpServletResponse var4) throws Exception;
```
* 定义视图导出接口（用于自定义视图生成规则）
```java
    public interface ExcelExportService {
        public void makeWorkBook(Map<String, Object> model, Workbook workbook);
    }
```
* 视图类
```java
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

    /******** setter/getter *********/
}
```
* 控制器中返回 Excel 视图
```java
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
```

## 上传文件
需要 Multipart 请求解析器 MultipartResolver，实现类 org.springframework.web.
    multipart.support.StandardServletMultipartResolver
#### 实例
* 配置 MultipartResolver，**注意！：id 是 Spring 默认，不能改变**
```xml
    <!-- id 不能变 -->
    <bean id="multipartResolver" 
        class="org.springframework.web.multipart.support.StandardServletMultipartResolver" />
```
* web.xml 配置解析器
```xml
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <load-on-startup>1</load-on-startup>

        <!-- 配置上传文件解析器 -->
        <multipart-config>
            <!-- 上传路径 -->
            <location>d:/project/Spring/SpringMVC组件开发</location>
            <!-- 单个文件大小 -->
            <max-file-size>5242880</max-file-size>
            <!-- 总文件大小 -->
            <max-request-size>10485760</max-request-size>
            <file-size-threshold>0</file-size-threshold>
        </multipart-config>
    </servlet>
```
* 上传文件表单
```html
    <!--                                                注意！：请求种类 -->
    <form method="post" action="../file/upload.form" enctype="multipart/form-data">
        <input type="file" name="file" value="请选择上传文件" />
        <input type="submit" value="提交" />
    </form>
```
* 控制器
```java
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
```
##### 执行过程
* 判断是否为 multipart 请求
* 如果是并且存在 id 为 multipartResolver 的 bean，就将 HttpServletRequest
    转换为 MultipartHttpServletRequest，该接口扩展了 MultipartHttpRequest 和关于文件操作的 
        MultipartRequst 接口
* 最后 DispatcherServlet 释放资源，把文件请求转换为 MultipartFile 对象