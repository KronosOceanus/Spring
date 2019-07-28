package object_factory;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Properties;

/**
 * 自定义（部分）对象工厂，用于创建结果集实例
 */
public class MyObjectFactory extends DefaultObjectFactory {

    Logger logger = Logger.getLogger(MyObjectFactory.class);

    private Object tmp = null;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        logger.info("初始化参数【" + properties + "】");
    }

    //方法 1（执行顺序）
    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        T result = super.create(type, constructorArgTypes, constructorArgs);
        logger.info("创建对象：" + result);
        tmp = result;
        return result;
    }
    //方法 2
    @Override
    public <T> T create(Class<T> type) {
        T result = super.create(type);
        logger.info("创建对象：" + result);
        logger.info("是否和上次创建的是同一个对象：【" + (tmp == result) + "】" );
        return result;
    }

    @Override
    public <T> boolean isCollection(Class<T> type) {
        return super.isCollection(type);
    }
}
