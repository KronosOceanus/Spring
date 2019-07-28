package auto_proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

//环绕通知
public class AutoAspect implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        System.out.println("全自动前方法！");

        //手动执行真实对象的方法
        Object obj = methodInvocation.proceed();

        System.out.println("全自动后方法！");
        return obj;
    }
}
