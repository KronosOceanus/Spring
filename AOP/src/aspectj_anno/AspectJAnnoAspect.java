package aspectj_anno;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect   //声明切面
public class AspectJAnnoAspect {

    @Before(value = "execution(* entity.UserServiceImpl.*(..))")
    public void myBefore(JoinPoint joinPoint){
        System.out.println("前置通知：" + joinPoint.getSignature().getName());
    }

    //声明公共切入点
    @Pointcut(value = "execution(* entity.UserServiceImpl.*(..))")
    private void myPointCut(){}

    @AfterReturning(value = "myPointCut()",returning = "ret")
    public void myAfterReturning(JoinPoint joinPoint,Object ret){
        System.out.println("后置通知：" + joinPoint.getSignature().getName() + "-->" + ret);
    }

    @Around(value = "myPointCut()")
    public Object myAround(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("环绕前：" + joinPoint.getSignature().getName());
        Object obj = joinPoint.proceed();
        System.out.println("环绕后：" + joinPoint.getSignature().getName());
        return obj;
    }

    @AfterThrowing(value = "myPointCut()",throwing = "e")
    public void myAfterThrowing(JoinPoint joinPoint,Throwable e){
        System.out.println("异常抛出后通知：" + joinPoint.getSignature().getName() + "" +
                "异常：" + e.getMessage());
    }

    @After(value = "myPointCut()")
    public void myAfter(JoinPoint joinPoint){
        System.out.println("最终通知：" + joinPoint.getSignature().getName());
    }
}
