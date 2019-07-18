package jdk_proxy;

import entity.UserService;
import entity.UserServiceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
