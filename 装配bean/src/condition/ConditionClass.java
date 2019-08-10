package condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 根据条件装配 bean
 * 需要被装配的 bean 添加 @Conditional 注解
 * 如果装配失败会抛出异常
 */
public class ConditionClass implements Condition {
    @Override              //运行时环境                       //获得关于该 bean 的注解信息
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        /**
         * 如果 bean 是引用类型，可以通过以下方法获得 bean 的属性
         * Environment env = conditionContext.getEnvironment();
         * String prop = env.getProperty("propId");
         */
        //是否创建 bean
        return true;
    }
}
