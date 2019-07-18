package life_cycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

//bean 的生命周期
public class Maker implements BeanNameAware, BeanFactoryAware, ApplicationContextAware,
        InitializingBean, DisposableBean {

    private String beverageShop = null;

    public void init(){
        System.out.println("【 " + this.getClass().getSimpleName() + " 】执行自定义初始化方法");
    }

    public void myDestory(){
        System.out.println("【 " + this.getClass().getSimpleName() + " 】执行自定义销毁方法");
    }

    public void make(){
        System.out.println("由 " + beverageShop + " 店铺提供");
    }

    @Override
    public void setBeanName(String s) {
        System.out.println("【 " + this.getClass().getSimpleName() + " 】执行 BeanNameAware 接口的 " +
                "setBeanName 方法");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("【 " + this.getClass().getSimpleName() + " 】执行 BeanFactoryAware 接口的 " +
                "setBeanFactory 方法");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("【 " + this.getClass().getSimpleName() + " 】执行 ApplicationContextAware 接口的 " +
                "setApplicationContext 方法");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("【 " + this.getClass().getSimpleName() + " 】执行 InitializingBean 接口的 " +
                "afterPropertiesSet 方法");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("【 " + this.getClass().getSimpleName() + " 】执行 DisposableBean 接口的 " +
                "destory 方法");
    }


















    public String getBeverageShop() {
        return beverageShop;
    }

    public void setBeverageShop(String beverageShop) {
        this.beverageShop = beverageShop;
    }

}
