package verifier;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ComponentScan(basePackages = {"verifier","entity"})
@EnableAspectJAutoProxy
public class VerifierConfig {
}
