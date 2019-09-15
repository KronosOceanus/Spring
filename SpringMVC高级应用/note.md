[TOC]
# SpringMVC 高级应用
## 数据转换和格式化
## 处理器执行流程
1. 得到 http 请求参数
2. 通过 HttpMessageConverter 进行消息转换（比较原始的消息转换）
3. 经过转换器和格式化器，转变为 java 对象
4. 验证
5. java 对象填充控制器（所以可以直接使用 bean 做参数）
6. 控制器处理结果通过 HttpMessageConverter 转换为其他类型
7. 视图解析和渲染

## 转换器分类
* Converter 接口定义的一对一转换器
* GenericConverter 接口定义的集合转换器

## FormattingConversionServiceFactoryBean 类注册转换器
XML 配置 <mvc:annotation-driven> / java 配置文件加入 @EnableWebMvc 之后，SpringIoC 容器会自动
    生成一个 FormattingConversionServiceFactoryBean 类的实例，通过该类的 converters 属性注册
        转换器

## HttpMessageConverter 和 json 消息转换器
#### HttpMessageConverter 源码
```java
    public interface HttpMessageConverter<T> {
        //判断类型是否可读，即转换器将请求信息转换为 var1 类型对象 
        boolean canRead(Class<?> var1, MediaType var2);
        //是否可写
        boolean canWrite(Class<?> var1, MediaType var2);
        //支持的媒体类型
        List<MediaType> getSupportedMediaTypes();
        /**
         * 读取数据类型，进行转换，HttpInputMessage 是 http 请求消息
         * 将 http 请求转换为 T 类型的对象
         */
        T read(Class<? extends T> var1, HttpInputMessage var2) throws IOException, HttpMessageNotReadableException;
        //将 T 类型的对象写入响应流中
        void write(T var1, MediaType var2, HttpOutputMessage var3) throws IOException, HttpMessageNotWritableException;
    }
```
#### 实例
* 配置 HttpMessageConverter
```xml
    <!-- 转换器：json 类型请求（由控制器得到 json 类型请求）转换为 json 数据 -->
    <bean id="jsonConverter" class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
        <property name="supportedMediaTypes">
            <list>
                <value>application/json;charset=UTF-8</value>
            </list>
        </property>
    </bean>
    <!-- 转换器注册 -->
    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
        <property name="messageConverters">
            <list>
                <ref bean="jsonConverter" />
            </list>
        </property>
    </bean>
``` 
* 测试
```java
    //测试 url：http://localhost:8080//SpringMVC_war_exploded/role/getRole?id=1
        @RequestMapping("/getRole")
        @ResponseBody //使 MVC 把结果转换为 json 类型响应，进而找到转换器
        public Role getRole(int id){
            return roleMapper.getRole(id);
        }]
```

## 一对一转换器
#### Converter 源码
```java
    public interface Converter<S, T> {
        //将 S（源类型）转换为 T（目标类型）
        T convert(S var1);
    }
```
#### 实例
* 自定义一对一转换器
```java
    /**
     * 自定义转换器（一对一）
     */
    public class StringToRoleConverter implements Converter<String, Role> {
        @Override
        public Role convert(String s) {
            if (StringUtils.isEmpty(s)){
                return null;
            }
            if (!s.contains("-")){
                return null;
            }
            String arr[] = StringUtils.delimitedListToStringArray(s, "-");
            if (arr.length != 3){
                return null;
            }
            Role role = new Role();
            role.setId(Integer.parseInt(arr[0]));
            role.setRoleName(arr[1]);
            role.setNote(arr[2]);
            return role;
        }
    }
```
* 注册转换器（在 dispatcher-servlet.xml 中），流程可以看本篇博客上面
```xml
                                     <!-- 生成接口（转换服务类）-->
     <mvc:annotation-driven conversion-service="conversionService" />
     
    <!-- 实现类，自动初始化该实例（因为配置了 mvc:annotation-driven）-->
    <!-- 相当于在注册 convert 方法（所有注册的转换器以重载方法的方式存在）-->
    <!-- 该实现类主要生产 DefaultFormattingConversionService 类对象 -->
    <bean id="conversionService" class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
        <property name="converters">
            <list>
                <bean class="converter.StringToRoleConverter" />
            </list>
        </property>
    </bean>
```
* 测试
```java
    //测试 url：http://localhost:8080//SpringMVC_war_exploded/role/updateRole.form?role=1-mvc-mvp!
    @RequestMapping("/updateRole")
    @ResponseBody
    public Map<String, Object> updateRole(Role role){
        Map<String ,Object> map = new HashMap<>();
        boolean updateFlag = (roleMapper.updateRole(role) == 1);
        map.put("success", updateFlag);
        if (updateFlag){
            map.put("msg", "更新成功");
        }else {
            map.put("msg", "更新失败");
        }
        return map;
    }
```

## 集合转换器
为了进行匹配类型判断，定义了如下两个接口：
* GenericConverter 接口
* ConditionalConverter 接口

为了简便，直接用 **ConditionalGenericConverter 接口**继承了上面两个接口，Spring 也为该
接口提供了很多实现类。
#### GenericConverter 接口源码
主要存放可转换的类型（由内部类维护两种类型），并执行转换操作
```java
    public interface GenericConverter {
        //返回可接受的转换类型
        Set<GenericConverter.ConvertiblePair> getConvertibleTypes();
        //转换方法（子类重写）
        Object convert(Object var1, TypeDescriptor var2, TypeDescriptor var3);
        //可转换匹配类
        public static final class ConvertiblePair {
            //源类型和目标类型
            private final Class<?> sourceType;
            private final Class<?> targetType;
            //构造器
            public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
                Assert.notNull(sourceType, "Source type must not be null");
                Assert.notNull(targetType, "Target type must not be null");
                this.sourceType = sourceType;
                this.targetType = targetType;
            }
    
            public Class<?> getSourceType() {
                return this.sourceType;
            }
    
            public Class<?> getTargetType() {
                return this.targetType;
            }
    
            /**
             * 重写 Object 类方法
             */
            //源类型和目标类型都相等则相同
            public boolean equals(Object other) {
                if (this == other) {
                    return true;
                } else if (other != null && other.getClass() == GenericConverter.ConvertiblePair.class) {
                    GenericConverter.ConvertiblePair otherPair = (GenericConverter.ConvertiblePair)other;
                    return this.sourceType == otherPair.sourceType && this.targetType == otherPair.targetType;
                } else {
                    return false;
                }
            }
    
            public int hashCode() {
                return this.sourceType.hashCode() * 31 + this.targetType.hashCode();
            }
    
            public String toString() {
                return this.sourceType.getName() + " -> " + this.targetType.getName();
            }
        }
    }
```
#### ConditionalConverter 接口源码
```java
    public interface ConditionalConverter {
        //这两种类型是否匹配
        boolean matches(TypeDescriptor var1, TypeDescriptor var2);
    }
```
#### 实现类 StringToArrayConverter 源码
```java
    final class StringToArrayConverter implements ConditionalGenericConverter {
        //转换器通过该成员变量注册
        private final ConversionService conversionService;
    
        public StringToArrayConverter(ConversionService conversionService) {
            this.conversionService = conversionService;
        }
        //可接受的类型（返回单例）
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(String.class, Object[].class));
        }
        //某两种类型是否可转换（被注册）
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return this.conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor());
        }
        //转换操作
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null) {
                return null;
            } else {
                String string = (String)source;
                //逗号分隔字符串
                String[] fields = StringUtils.commaDelimitedListToStringArray(string);
                //初始化目标类
                Object target = Array.newInstance(targetType.getElementTypeDescriptor().getType(), fields.length);
                //遍历字符串数组
                for(int i = 0; i < fields.length; ++i) {
                    String sourceElement = fields[i];
                    //调用转换方法
                    Object targetElement = this.conversionService.convert(sourceElement.trim(), sourceType, targetType.getElementTypeDescriptor());
                    //填充目标类对象
                    Array.set(target, i, targetElement);
                }
    
                return target;
            }
        }
    }
```


