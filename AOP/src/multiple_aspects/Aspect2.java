package multiple_aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;

public class Aspect2 {

    public void myBefore(JoinPoint joinPoint){
        System.out.println("前置通知-2：" + joinPoint.getSignature().getName());
    }

}
