package chain;

import java.lang.reflect.Method;

/**
 * 责任链模式
 * 一个对象在一条链上被多个拦截器处理
 * 优点：可以增加新的拦截器
 * 缺点：反射和代理性能不高
 * 设置 before 方法返回为 true 才会继续执行真是对象方法（才能继续触发上一个拦截器）
 */
public interface Interceptor {

    /**
     * @param proxy 代理对象
     * @param target 真实对象
     * @param method 真实对象的方法
     * @param args 参数
     * @return true 执行真实对象方法，false 执行 around 方法
     */

    //真实对象方法前执行，如果返回值为 true 执行真实对象方法
     boolean before(Object proxy, Object target, Method method, Object[] args);
     // false 执行 around 方法
     void around(Object proxy, Object target, Method method, Object[] args);
     //最后执行
     void after(Object proxy, Object target, Method method, Object[] args);
}
