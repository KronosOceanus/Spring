package annotation;

import org.springframework.context.annotation.ComponentScan;

/**
 * 配置类
 * 扫描当前包下的 bean @ComponentScan
 * 扫描指定类 @ComponentScan(basepackageClasses = {Human.class, StudentAction.class})
 * 扫描指定包（多个）
 */
@ComponentScan(basePackages = {"annotation", "entity"})
public class AnnoConfig {
}
