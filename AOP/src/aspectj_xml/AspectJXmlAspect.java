package aspectj_xml;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

public class AspectJXmlAspect {

    //通知方法的参数（描述真实方法，比如真实方法的方法名）
    public void myBefore(JoinPoint joinPoint){
        System.out.println("前置通知：" + joinPoint.getSignature().getName());
    }
    //第二个参数在 XML 中配置，代表真实方法的返回值
    public void myAfterReturning(JoinPoint joinPoint,Object ret){
        System.out.println("后置通知：" + joinPoint.getSignature().getName() + "-->" + ret);
    }
    //环绕通知（注意参数类型）
    public Object myAround(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("环绕前：" + joinPoint.getSignature().getName());
        Object obj = joinPoint.proceed();
        System.out.println("环绕后：" + joinPoint.getSignature().getName());
        return obj;
    }
    //第二个参数在 XML 中配置，描述异常
    public void myAfterThrowing(JoinPoint joinPoint,Throwable e){
        System.out.println("异常抛出后通知：" + joinPoint.getSignature().getName() + "" +
                "异常：" + e.getMessage());
    }
    //最终通知
    public void myAfter(JoinPoint joinPoint){
        System.out.println("最终通知：" + joinPoint.getSignature().getName());
    }
}
