package chain;

import interceptor.Interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//在 JDK 代理中使用拦截器
public class InterceptorJdkProxy implements InvocationHandler {

    //保存真实对象，拦截器的全限定类名
    private Object target;
    private String interceptorClass = null;

    public InterceptorJdkProxy(Object target, String interceptorClass){
        this.target = target;
        this.interceptorClass = interceptorClass;
    }

    //建立联系，并得到代理对象（注意这个是静态方法）
    public static Object bind(Object target, String interceptorClass){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InterceptorJdkProxy(target,interceptorClass)); //要设置参数
    }

    //代理方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (interceptorClass == null){
            //没有设置拦截器，直接反射原来方法
            return method.invoke(target,args);
        }

        Object result = null;
        //得到拦截器对象
        Interceptor interceptor =
                (Interceptor)Class.forName(interceptorClass).getDeclaredConstructor().newInstance();
        //拦截器执行过程
        if (interceptor.before(proxy,target,method,args)){
            result = method.invoke(target,args);
        }else {
            interceptor.around(proxy,target,method,args);
        }
        interceptor.after(proxy,target,method,args);
        return result;
    }
}
