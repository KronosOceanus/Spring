[TOC]
# AOP
采用横向抽取，将前后方法与核心方法分离，交给Spring代理去完成组合（即切面），
加强代码重用性，取代了传统继承体系的重复性代码

## AOP 术语
#### target
真实对象（需要被代理的类）
#### JoinPoint（连接点）
可能被拦截到的方法（所有方法）
#### PointCut（切入点）
被增强的连接点（某个方法）
#### advice（通知）
增强代码，例如前后方法
#### Weaving（织入）
把 advice 用于 target 来创建 proxy 的过程
#### proxy（代理）
#### Aspect（切面）
PointCut 和 advice 的结合，无法获得 http 原始请求与相应对象

## jdk 动态代理
对*装饰者模式*的简化，流程
* 目标类：接口 + 实现类
* 切面类：用于存放通知（增强代码），例如后处理 bean 类
```java
    //切面类（增强代码）
    public class JDKAspect {
    
        public void before(){
            System.out.println("before!");
        }
    
        public void after(){
            System.out.println("after!");
        }
    }
```
* 工厂类：编写工厂类生成代理
```java
    //工厂类
    public class JDKBeanFactory {
    
        //生成代理类
        public static UserService createService(){
            //目标类
            UserService target = new UserServiceImpl();
            //切面类
            JDKAspect jdkAspect = new JDKAspect();
            //结合以上生成代理类（切面）
            UserService proxy = (UserService) Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(),
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            jdkAspect.before();
                            Object obj = method.invoke(target, args);
                            jdkAspect.after();
                            return obj;
                        }
                    }
            );
            return proxy;
        }
    }
```
* 测试

## cglib 动态代理
目标类没有实现接口，编写过程与 jdk 动态代理相似

## 半自动代理（FactoryBean）
让 Spring 创建代理对象，手动从 Spring 获取代理对象，导入 jar
* 核心 4 + 1
* aopaliance（aop 联盟）
* aop（aop 实现）

实现：
* 目标类
* 切面类（要实现 aopaliance 包下的 MethodInterceptor 环绕通知接口），例
```java
    /**
     * 切面类中确定通知，需要实现不同接口，从而确定方法名称
     * 注意！：是 aopalliance 包下的环绕通知（不是 cglib 包下的）
     */
    public class FactoryBeanAspect implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            System.out.println("前方法！");
    
            //手动执行目标方法
            Object obj = methodInvocation.proceed();
    
            System.out.println("后方法！");
            return obj;
        }
    }
```
* XML 文件配置创建代理
```xml
    <!-- 1.创建目标类 -->
    <bean id="userService" class="entity.UserServiceImpl" />
    <!-- 2.创建切面类 -->
    <bean id="factoryBeanAspect" class="factory_bean_proxy.FactoryBeanAspect" />
    <!-- 3.创建代理类
        使用工厂 bean，底层调用 getObject 方法，得到一个特殊的 bean
        如下工厂 bean，用于生成代理对象 -->
    <bean id="userServiceProxy" class="org.springframework.aop.framework.ProxyFactoryBean">
        <!-- 真实对象实现的接口          全限定类名 -->
        <property name="interfaces" value="entity.UserService" />
        <!-- 真实对象               对象引用-->
        <property name="target" ref="userService" />
        <!-- 拦截器名称                      类型是 String[] -->
        <property name="interceptorNames" value="factoryBeanAspect" />
        <!-- 表示底层使用 cglib 代理（默认根据真实对象是否有接口选择底层代理方式） -->
        <property name="optimize" value="true" />
    </bean>
```
* 测试
### Advice 通知 API
* MethodBeforeAdvice：前置通知
* MethodReturningAdvice：后置通知
* **MethodInterceptor：环绕通知**：前后通知（必须手动执行）
* ThrowsInterceptor：异常通知
* IntroductionInterceptor：引介通知 ？

## 全自动代理
与半自动代理的不同：
* 还需要导入 aspectj-weaver（织入） 包（用到 AspectJ 框架/idea 中需手动导入）
* 代码方面，XML 配置不同，其他基本相同
* 直接获取真实对象，自动返回代理对象
#### XML 配置文件
```xml
    <!-- 1.创建目标类 -->
    <bean id="userService" class="entity.UserServiceImpl" />
    <!-- 2.创建切面类 -->
    <bean id="autoAspect" class="auto_proxy.AutoAspect" />
    <!-- 3.aop 编程
        3.1 导入命名空间（aop）
        3.2 使用 <aop:config> 进行配置
        -->
                <!-- 设置为 cglib 代理 -->
    <aop:config proxy-target-class="true">
        <!-- 切入点，从目标对象获得具体方法
            expression 中是切入点表达式，选择方法 -->
        <aop:pointcut id="pointCut"
                      expression="execution(* entity.UserServiceImpl.*(..))" />
                                <!-- 返回值任意 包名       类名  方法任意 参数任意 -->
        <!-- 特殊的切面，只有一个通知和一个切入点 -->
        <aop:advisor advice-ref="autoAspect" pointcut-ref="pointCut"/>
    </aop:config>
```

# AspectJ（AOP 框架）
## 切入点表达式
execution() 语法：
```xml
    execution(修饰符 返回值 包.类.方法(参数)throws异常)
```
* .* 表示任意/儿子
* .. 表示自己和子孙/任意参数
例如：
* User*：以User为开头
* *Impl：以Impl为结尾

还可以在 XML 中写多个表达式，匹配多个
```xml
    <aop:pointcut expression="execution() || execution()" id="myPointCut" />
```
### 其他表达式
* within(包名.*)：匹配包或子包中的方法
* this(包名.接口)：匹配实现接口的代理对象中的方法
* target(包名.接口)：匹配实现接口的真实对象中的方法
* args(参数类型)：匹配参数格式符合标准的方法
* bean("id")：对指定 bean 所有的方法

## AspectJ 通知类型
不同：
* aopalliance：实现接口，方法名确定
* aspectj：自定义名称
### AspectJ 通知
* before
* afterReturning
* **around：环绕通知**
* afterThrowing
* after：最终通知（finally 中执行） 

#### XML 中使用
```xml
    <aop:config>
        <!-- 引用切面类 -->
        <aop:aspect ref="aspectjAspect">
            <!-- 设置切点 -->
            <aop:pointcut id="pointCut" expression="execution(* entity.UserServiceImpl.*(..))" />
            <!-- 前置通知   方法名        切入点的引用（可以与其他通知共享） -->
            <aop:before method="myBefore" pointcut-ref="pointCut" />
            <!-- 后置通知 -->
            <aop:after-returning method="myAfterReturning" pointcut-ref="pointCut"
                returning="ret"/>  <!-- returning 确定第二个参数名/Object（代表真实方法的返回值） -->
            <!-- 环绕通知 -->
            <aop:around method="myAround" pointcut-ref="pointCut" />
            <!-- 异常抛出 -->
            <aop:after-throwing method="myAfterThrowing" pointcut-ref="pointCut"
                throwing="e"/>  <!-- throwing 确定第二个参数名/Throwable（代表异常） -->
            <!-- 其他通知类似 -->
        </aop:aspect>
    </aop:config>
```
#### 注解使用
* 切面类（注意要声明切面）
```java
    @Component
    @Aspect   //声明切面
    public class AspectJAnnoAspect {

        @Before(value = "execution(* entity.UserServiceImpl.*(..))")
        public void myBefore(JoinPoint joinPoint){
            System.out.println("前置通知：" + joinPoint.getSignature().getName());
        }
    
        //声明公共切入点
        @Pointcut(value = "execution(* entity.UserServiceImpl.*(..))")
        private void myPointCut(){}
    
        @AfterReturning(value = "myPointCut()",returning = "ret")
        // ret 代表真实方法的返回值
        public void myAfterReturning(JoinPoint joinPoint,Object ret){
            System.out.println("后置通知：" + joinPoint.getSignature().getName() + "-->" + ret);
        }
    
        @Around(value = "myPointCut()") //参数特殊
        public Object myAround(ProceedingJoinPoint joinPoint) throws Throwable{
            System.out.println("环绕前：" + joinPoint.getSignature().getName());
            Object obj = joinPoint.proceed();
            System.out.println("环绕后：" + joinPoint.getSignature().getName());
            return obj;
        }
    
        @AfterThrowing(value = "myPointCut()",throwing = "e")
        public void myAfterThrowing(JoinPoint joinPoint,Throwable e){
            System.out.println("异常抛出后通知：" + joinPoint.getSignature().getName() + "" +
                    "异常：" + e.getMessage());
        }
    
        @After(value = "myPointCut()")
        public void myAfter(JoinPoint joinPoint){
            System.out.println("最终通知：" + joinPoint.getSignature().getName());
        }
    }
```
* 配置类（启用 AspectJ 注解扫描）
```java
    @ComponentScan(basePackages = {"aspectj_anno","entity"})
    @EnableAspectJAutoProxy  //启动 AspectJ 自动代理
    public class ApplicationConfig {
    }
```

## 引入
用动态代理在目标方法类中引入新的接口（检测器）
* 检测器接口
```java
    public interface UserVerifier {
    
        boolean isNotNull(UserService userService);
    }
```
* 检测器实现
```java
    public class UserVerifierImpl implements UserVerifier{
    
        @Override
        public boolean isNotNull(UserService userService) {
            return userService != null;
        }
    }
```
* 在切面类中引入检测器
```java
    @Component
    @Aspect
    public class VerifierAspect {
    
        //切面中引入检测器   增强目标方法类，在该类引入检测器接口       检测器默认实现
        @DeclareParents(value = "entity.UserServiceImpl+",defaultImpl = UserVerifierImpl.class)
        public UserVerifier userVerifier;
    }
```
然后就可把原有对象强转为检测器类型，调用检测器方法，例：
```java
    UserService userService = applicationContext.getBean("userService",UserService.class);
    UserVerifier userVerifier = (UserVerifier) userService;
    if (userVerifier.isNotNull(userService)){
        userService.addUser();
    }
```

## 多个切面
需要按照一定顺序执行，设定顺序有两种方法
* XML 设置
```xml
    <!-- 通过 order 属性设置 -->
    <aop:aspect ref="aspect1" order="1">
        <aop:before method="myBefore" pointcut-ref="pointCut" />
    </aop:aspect>
```
* 注解设置
```java
    @Order(1)
    public class Aspect1 {
        public void myBefore(JoinPoint joinPoint){
            System.out.println("前置通知-1：" + joinPoint.getSignature().getName());
        }
    }
```












