package cglib_proxy;

import entity.User;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLIBBeanFactory {

    public static User createUser(){
        final User target = new User();
        final CGLIBAspect cglibAspect = new CGLIBAspect();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            //等价 invoke 方法（第一个参数是 proxy）
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                cglibAspect.before();
                //执行 proxy 的父类
                Object obj = methodProxy.invokeSuper(o, objects);
                cglibAspect.after();
                return obj;
            }
        });
        return (User)enhancer.create();
    }
}
