package jdk_proxy;

//切面类（增强代码）
public class JDKAspect {

    public void before(){
        System.out.println("before!");
    }

    public void after(){
        System.out.println("after!");
    }
}
