package cn.xuele.minispring.beans.factory.support;

import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.core.io.Resource;
import cn.xuele.minispring.core.io.ResourceLoader;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public interface BeanDefinitionReader {

    // 获取注册表
    BeanDefinitionRegistry getRegistry();

    // 获取资源加载器
    ResourceLoader getResourceLoader();

    /**
     * 从 resource 中加载 BeanDefinition
     *
     * @param resource 资源
     */
    void loadBeanDefinitions(Resource resource) throws BeansException;

    /**
     * 允许一次性加载多个资源文件
     *
     * @param resources 资源
     */
    void loadBeanDefinitions(Resource... resources) throws BeansException;

    /**
     * 从字符串路径中加载 BeanDefinition
     *
     * @param location 资源路径
     */
    void loadBeanDefinitions(String location) throws BeansException;

    void loadBeanDefinitions(String... locations) throws BeansException;
}
