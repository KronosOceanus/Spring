package factory_bean_proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 切面类中确定通知，需要实现不同接口，从而确定方法名称
 * 注意！：是 aopalliance 包下的环绕通知（不是 cglib 包下的）
 */
public class FactoryBeanAspect implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("半自动前方法！");

        //手动执行目标方法
        Object obj = methodInvocation.proceed();

        System.out.println("半自动后方法！");
        return obj;
    }
}
