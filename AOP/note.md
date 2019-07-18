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
PointCut 和 advice 的结合

## jdk 动态代理
对*装饰者模式*的简化，流程
1. 目标类：接口 + 实现类
2. 切面类：用于存放通知（增强代码），例如后处理 bean 类
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
3. 工厂类：编写工厂类生成代理
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
4. 测试

## cglib 动态代理
目标类没有实现接口，编写过程与 jdk 动态代理相似

## 半自动代理
让 Spring 创建代理对象，手动从 Spring 获取代理对象，导入 jar
* 核心 4 + 1
* aopaliance（aop 联盟）
* aop（aop 实现）

实现：
1. 目标类
2. 切面类（要实现 aopaliance 包下的 MethodInterceptor 环绕通知接口），例
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
3. XML 文件配置创建代理
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
4. 测试
### Advice 通知 API
###### MethodBeforeAdvice 前置通知
###### MethodReturningAdvice 后置通知
###### **MethodInterceptor 环绕通知**
前后通知
###### ThrowsInterceptor 异常通知
###### IntroductionInterceptor 引介通知 ？









