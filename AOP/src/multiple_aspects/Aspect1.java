package multiple_aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;

//可以通过该注解完成多个切面顺序执行 @Order(1)
public class Aspect1 {

    public void myBefore(JoinPoint joinPoint){
        System.out.println("前置通知-1：" + joinPoint.getSignature().getName());
    }

}
