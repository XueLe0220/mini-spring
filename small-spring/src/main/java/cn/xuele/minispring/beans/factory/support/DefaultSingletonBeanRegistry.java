package cn.xuele.minispring.beans.factory.support;

import cn.xuele.minispring.beans.factory.DisposableBean;
import cn.xuele.minispring.beans.factory.config.ObjectFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author XueLe
 * @since 2025/12/8
 */
public class DefaultSingletonBeanRegistry {

    // 一级缓存
    private final Map<String, Object> singletonObjects = new HashMap<>();
    // 二级缓存
    private final Map<String, Object> earlySingletonObjects = new HashMap<>();
    // 三级缓存
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();

    // 存储容器关闭时需要销毁的 Bean
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    // 将创建好的 bean 存入一级缓存
    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
        // Bean 创建完成，从二三级缓存中移除
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    // 添加三级缓存
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        if (!this.singletonObjects.containsKey(beanName)) {
            this.singletonFactories.put(beanName, singletonFactory);
        }
        // 删除二级缓存的旧缓存。
        this.earlySingletonObjects.remove(beanName);
    }

    protected Object getSingleton(String name) {
        // 1. 先在一级缓存找
        Object singletonObject = singletonObjects.get(name);

        // 2. 一级缓存没有 就进二级缓存找
        if (singletonObject == null) {
            singletonObject = earlySingletonObjects.get(name);
        }

        // 3. 二级也没有，就去三级里面找
        if (singletonObject == null) {
            ObjectFactory<?> singletonFactory = singletonFactories.get(name);
            if (singletonFactory != null) {
                // 3.1 找到工厂，让工厂生产对象
                singletonObject = singletonFactory.getObject();

                // 3.2 拿到对象，放入二级缓存
                earlySingletonObjects.put(name, singletonObject);

                // 3.3 代理创建完成，移除工厂
                singletonFactories.remove(name);
            }
        }
        return singletonObject;
    }

    protected void addDisposableBean(String name, DisposableBean bean) {
        disposableBeans.put(name, bean);
    }

    protected void destroySingletons() {
        Set<String> keySet = disposableBeans.keySet();
        Object[] disposableBeanNames = keySet.toArray();

        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            Object disposableBeanName = disposableBeanNames[i];
            DisposableBean removedBean = disposableBeans.remove(disposableBeanName);

            try {
                removedBean.destroy();
            } catch (Exception e) {
                System.out.println("Destroy method on bean '" + disposableBeanName + "' threw an exception: " + e);
            }
        }
    }
}
