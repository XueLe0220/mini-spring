package cn.xuele.minispring.beans.factory.support;

import cn.xuele.minispring.beans.factory.config.BeanDefinition;

/**
 * Bean 定义注册表接口
 * 核心功能：注册、查询 BeanDefinition
 *
 * @author XueLe
 */
public interface BeanDefinitionRegistry {

    /**
     * 向注册表中注册 BeanDefinition
     *
     * @param beanName       Bean 名称
     * @param beanDefinition Bean 定义
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 判断是否包含指定名称的 BeanDefinition
     */
    boolean containsBeanDefinition(String beanName);
}
