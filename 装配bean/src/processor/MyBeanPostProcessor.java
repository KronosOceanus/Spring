package processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyBeanPostProcessor implements BeanPostProcessor {

    //在所有的 bean 初始化前后执行
    //要想匹配单个 bean，则使用 equals 方法判断
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        System.out.println("初始化前方法" + s);
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        System.out.println("初始化后方法" + s);
        return o;
        /**
        //生成代理并返回（真实对象引用消失，销毁）
        return Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(),
                o.getClass().getInterfaces(), new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object obj = method.invoke(o,args);
                        return obj;
                    }
                });
         */
    }
}
