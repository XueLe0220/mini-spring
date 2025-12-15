package cn.xuele.minispring.beans.factory;

import cn.xuele.minispring.beans.factory.config.BeanPostProcessor;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public interface ConfigurableBeanFactory {
    /**
     * 销毁随容器消亡的 Bean
     */
    void close();

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
}
