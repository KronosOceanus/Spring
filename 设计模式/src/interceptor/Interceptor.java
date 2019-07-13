package interceptor;

import java.lang.reflect.Method;

//拦截器进一步简化动态代理
public interface Interceptor {

    /**
     * @param proxy 代理对象
     * @param target 真实对象
     * @param method 真实对象的方法
     * @param args 参数
     * @return true 执行真实对象方法，false 执行 around 方法
     */

    //真实对象方法前执行，如果返回值为 true 执行真实对象方法
     boolean before(Object proxy, Object target, Method method,Object[] args);
     // false 执行 around 方法
     void around(Object proxy, Object target, Method method,Object[] args);
     //最后执行
     void after(Object proxy, Object target, Method method,Object[] args);
}
