package condition;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * 条件化装配 bean
 */
@Component
public class Human {

    @Value("cs")
    private String name;

    @Bean("humanName")
    @Conditional(ConditionClass.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
