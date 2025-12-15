package cn.xuele.minispring.beans.factory.support;


import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.beans.factory.BeanFactory;
import cn.xuele.minispring.beans.factory.BeanFactoryAware;
import cn.xuele.minispring.beans.factory.ConfigurableBeanFactory;
import cn.xuele.minispring.beans.factory.ListableBeanFactory;
import cn.xuele.minispring.beans.factory.config.BeanDefinition;
import cn.xuele.minispring.beans.factory.config.BeanReference;
import cn.xuele.minispring.beans.factory.DisposableBean;
import cn.xuele.minispring.beans.factory.InitializingBean;
import cn.xuele.minispring.beans.PropertyValue;
import cn.xuele.minispring.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认的 Bean 工厂实现类
 * 具备注册 BeanDefinition 和 实例化 Bean 的能力
 *
 * @author XueLe
 */
public class DefaultListableBeanFactory extends DefaultSingletonBeanRegistry implements ListableBeanFactory, ConfigurableBeanFactory, BeanDefinitionRegistry {


    // 存储 Bean 定义的容器
    private final Map<String, BeanDefinition> beanMap = new HashMap<>();

    // 存储 Bean 后置处理器
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    // 根据bean名称返回bean
    @Override
    public Object getBean(String name) {

        // 查询 bean 缓存，实现单例模式
        Object singleton = getSingleton(name);
        if (singleton != null) {
            return singleton;
        }


        // 获取 bean 定义，若没有 则抛出异常
        BeanDefinition beanDefinition = beanMap.get(name);
        if (null == beanDefinition) {
            throw new BeansException("No bean named '" + name + "' is defined");
        }

        // 实例化并填充属性
        try {
            Class<?> beanClass = beanDefinition.getBeanClass();
            // 实例化--暂时只支持无参构造
            Object bean = beanClass.getConstructor().newInstance();


            // 属性填充
            applyPropertyValues(bean, beanDefinition);

            // bean 初始化 在这一步完成对实例化 bean 的后置处理
            bean = initializeBean(name, bean, beanDefinition);

            // 查询是否需要随容器消亡
            if (bean instanceof DisposableBean) {
                addDisposableBean(name, (DisposableBean) bean);
            }

            // 添加进注册表
            addSingleton(name, bean);


            return bean;
        } catch (Exception e) {
            throw new BeansException("Failed to instantiate bean '" + name + "'", e);
        }
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanMap.put(name, beanDefinition);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanMap.containsKey(beanName);
    }

    @Override
    public void close() {
        destroySingletons();
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }


    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> result = new HashMap<>();
        // 1. 遍历 beanMap (我们存储 BeanDefinition 的地方)
        beanMap.forEach((beanName, beanDefinition) -> {
            Class<?> beanClass = beanDefinition.getBeanClass();
            if (type.isAssignableFrom(beanClass)) {
                result.put(beanName, (T) getBean(beanName));
            }
        });
        return result;
    }

    /**
     * 核心实现：应用属性值
     * <p>
     * 遍历 BeanDefinition 中的属性列表，利用反射机制，
     * 暴力访问（setAccessible）对象的私有字段并赋值。
     *
     * @param bean           已实例化的 Bean 对象
     * @param beanDefinition Bean 的定义信息
     * @throws Exception 反射操作异常
     */
    private void applyPropertyValues(Object bean, BeanDefinition beanDefinition) throws Exception {

        // 获取属性列表
        List<PropertyValue> propertyValueList = beanDefinition.getPropertyValueList();

        for (PropertyValue propertyValue : propertyValueList) {
            String name = propertyValue.getName();
            Object value = propertyValue.getValue();


            // 1. 检查：value 是否为 bean 引用
            if (value instanceof BeanReference) {
                BeanReference reference = (BeanReference) value;
                String beanName = reference.getBeanName();
                value = getBean(beanName);
            }

            // 2. 获取待填充字段
            Field field = bean.getClass().getDeclaredField(name);

            // 3. 填充字段
            field.setAccessible(true);
            field.set(bean, value);

        }
    }

    /**
     * bean 初始化
     * 遍历 beanPostProcessors(bean 后置处理器), 对已经实例化的 bean 进行处理
     *
     * @param beanName       -  bean名称
     * @param bean           - 实例化的 bean
     * @param beanDefinition = bean 的定义信息
     * @return
     */
    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {

        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }


        // 前置处理
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object current = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
            if (current != null) {
                bean = current;
            }
        }

        // bean 初始化
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }

        // 后置处理
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object current = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            if (current != null) {
                bean = current;
            }
        }

        return bean;

    }

}
