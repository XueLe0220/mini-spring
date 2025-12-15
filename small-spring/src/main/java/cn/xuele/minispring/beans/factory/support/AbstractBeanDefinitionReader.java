package cn.xuele.minispring.beans.factory.support;

import cn.xuele.minispring.core.io.DefaultResourceLoader;
import cn.xuele.minispring.core.io.ResourceLoader;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private final BeanDefinitionRegistry registry;
    private ResourceLoader resourceLoader;

    // 构造函数强制要求传入 registry
    protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, new DefaultResourceLoader());
    }

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

}
