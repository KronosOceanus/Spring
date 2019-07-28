package multiple_aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;


public class Aspect3 {

    public void myBefore(JoinPoint joinPoint){
        System.out.println("前置通知-3：" + joinPoint.getSignature().getName());
    }

}
