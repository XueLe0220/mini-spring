package cn.xuele.minispring.beans.factory;

import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.beans.factory.config.BeanDefinition;

/**
 * 配置化 & 可列表化 的 Bean 工厂接口
 *
 * @author XueLe
 */
public interface ConfigurableListableBeanFactory
        extends ListableBeanFactory, ConfigurableBeanFactory {

    /**
     * 根据名称查找 BeanDefinition
     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /**
     * 提前实例化所有单例实例
     */
    void preInstantiateSingletons() throws BeansException;
}