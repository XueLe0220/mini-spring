package cn.xuele.minispring.context;

import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.beans.factory.ConfigurableListableBeanFactory;
import cn.xuele.minispring.beans.factory.config.BeanPostProcessor;
import cn.xuele.minispring.core.io.DefaultResourceLoader;

import java.util.Collection;
import java.util.Map;


/**
 * 核心抽象类：定义了 Spring 容器启动的“标准 SOP”
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    @Override
    public void refresh() throws BeansException {
        // 1.1 初始化工厂,从资源加载 beanDefinition(由子类实现)
        refreshBeanFactory();
        // 1.2 获取刚创建好的工厂
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        // 2. 注册 BeanPostProcessor(自动创建和激活扩展点)
        registerBeanPostProcessors(beanFactory);

        // 3. 提前实例化所有单例 Bean
        beanFactory.preInstantiateSingletons();
    }

    @Override
    public void close() {
        getBeanFactory().close();
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {

        Map<String, BeanPostProcessor> bppMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor value : bppMap.values()) {
            beanFactory.addBeanPostProcessor(value);
        }

    }

    public abstract ConfigurableListableBeanFactory getBeanFactory();

    protected abstract void refreshBeanFactory();
}