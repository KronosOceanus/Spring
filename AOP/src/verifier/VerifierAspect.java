package verifier;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class VerifierAspect {

    //切面中引入检测器   增强目标方法类，在该类引入检测器接口       检测器默认实现
    @DeclareParents(value = "entity.UserServiceImpl+",defaultImpl = UserVerifierImpl.class)
    public UserVerifier userVerifier;
}
