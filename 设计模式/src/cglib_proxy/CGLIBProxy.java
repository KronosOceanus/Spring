package cglib_proxy;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLIBProxy implements MethodInterceptor {

    //生成 CGLIB 代理对象
    public Object getProxy(Class cls){
        //增强类
        Enhancer enhancer = new Enhancer();
        //看作真实对象的子类
        enhancer.setSuperclass(cls);
        //设置回调intercept方法
        enhancer.setCallback((Callback) this);
        //创建代理
        return enhancer.create();
    }

    /**
     *
     * @param o 代理对象（增强类）
     * @param method 真实对象方法
     * @param objects 参数
     * @param methodProxy 代理方法（增强类方法），它的父类方法即真实对象的方法
     * @return 方法逻辑
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return methodProxy.invokeSuper(o,objects);
    }
}
