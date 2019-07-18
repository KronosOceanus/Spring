package cglib_proxy;
//切面类
public class CGLIBAspect {

    public void before(){
        System.out.println("before!");
    }

    public void after(){
        System.out.println("after!");
    }
}
