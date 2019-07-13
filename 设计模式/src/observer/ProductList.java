package observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 观察者模式
 * 编写被观察者与观察者
 * 通过观察者建立联系，更新的时候通知观察者调用对应的方法
 */
//被观察者
public class ProductList extends Observable {
    //产品列表
    private static List<String> productList = null;
    //单例化商品列表
    private static ProductList instance;

    private ProductList(){}

    public static ProductList getInstance(){
        if (instance == null){
            instance = new ProductList();
            productList = new ArrayList<>();
            return instance;
        }else {
            return instance;
        }
    }

    public void addProductListObserver(Observer observer){
        this.addObserver(observer);
    }

    /**
     * 重要
     */
    public void addProduct(String newProduct){
        productList.add(newProduct);
        System.out.println("产品列表新增了【" + newProduct + "】产品！");
        //被观察者发生变法
        this.setChanged();
        //通知所有观察者，传递新产品
        this.notifyObservers(newProduct);
    }
}
