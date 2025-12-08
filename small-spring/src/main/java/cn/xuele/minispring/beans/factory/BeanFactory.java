package cn.xuele.minispring.beans.factory;

import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.beans.factory.config.BeanDefinition;

/**
 * Spring 容器的顶层接口
 * 定义了获取 Bean 的基础规范
 *
 * @author XueLe
 */
public interface BeanFactory {

    /**
     * 根据 Bean 的名称获取实例
     *
     * @param name Bean 的唯一标识
     * @return Bean 实例
     * @throws BeansException 如果 Bean 不存在或创建失败
     */
    Object getBean(String name) throws BeansException;

    /**
     * 根据 Bean 的名称注册 Bean 定义
     *
     * @param beanDefinition
     * @param name
     */
    void registerBeanDefinition(BeanDefinition beanDefinition, String name);

    /**
     * 销毁随容器消亡的 Bean
     */
    void close();
}
