import cglib_proxy.CGLIBProxy;
import entity.People;
import interceptor.InterceptorJdkProxy;
import entity.Dog;
import entity.DogImpl;
import jdk_proxy.JDKProxy;
import observer.JingDongObserver;
import observer.ProductList;
import observer.TaoBaoObserver;

public class Test {

    // JDK 代理测试
    private void test01(){
        JDKProxy jdkProxy = new JDKProxy();
        Dog proxy = (Dog) jdkProxy.bind(new DogImpl());
        proxy.run();
    }
    // CGLIB 代理测试
    private void test02(){
        CGLIBProxy cglibProxy = new CGLIBProxy();
        People proxy = (People) cglibProxy.getProxy(People.class);
        proxy.eat();
    }
    //拦截器测试
    private void test03(){
        //必须是全限定类名
        Dog proxy = (Dog) InterceptorJdkProxy.bind(new DogImpl(),
                "interceptor.MyInterceptor");
        proxy.run();
    }
    //责任链测试
    private void test04(){
        Dog proxy1 = (Dog)InterceptorJdkProxy.bind(new DogImpl(),
                "chain.Interceptor1");
        Dog proxy2 = (Dog)InterceptorJdkProxy.bind(proxy1,
                "chain.Interceptor2");
        Dog proxy3 = (Dog)InterceptorJdkProxy.bind(proxy2,
                "chain.Interceptor3");
        proxy3.run();
    }
    //观察者测试
    private void test05(){
        //得到商品列表
        ProductList observable = ProductList.getInstance();
        //获得并设置观察者
        TaoBaoObserver taoBaoObserver = new TaoBaoObserver();
        JingDongObserver jingDongObserver = new JingDongObserver();
        observable.addObserver(taoBaoObserver);
        observable.addObserver(jingDongObserver);
        //添加商品
        observable.addProduct("新增产品");
    }


    public static void main(String[] args) {
        Test t = new Test();
        t.test01();
        t.test02();
        t.test03();
        t.test04();
        t.test05();
    }
}
