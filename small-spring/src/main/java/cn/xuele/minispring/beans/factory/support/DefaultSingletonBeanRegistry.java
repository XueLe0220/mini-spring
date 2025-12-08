package cn.xuele.minispring.beans.factory.support;

import cn.xuele.minispring.beans.factory.DisposableBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author XueLe
 * @since 2025/12/8
 */
public class DefaultSingletonBeanRegistry {
    private final Map<String, Object> beanRegistry = new HashMap<>();

    // 存储容器关闭时需要销毁的 Bean
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    protected void addSingleton(String name, Object bean) {
        beanRegistry.put(name, bean);
    }

    protected Object getSingleton(String name) {
        return beanRegistry.get(name);
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
