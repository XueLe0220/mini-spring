package core.factory;


import core.bean.BeanDefinition;
import core.bean.BeanReference;
import core.bean.DisposableBean;
import core.bean.InitializingBean;
import core.bean.PropertyValue;
import core.exception.BeansException;
import core.processor.BeanPostProcessor;
import core.registry.DefaultSingletonBeanRegistry;

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
public class DefaultListableBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {


    // 存储 Bean 定义的容器
    private final Map<String, BeanDefinition> beanMap = new HashMap<>();

    // 存储 Bean 后置处理器
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    // 根据bean名称返回bean
    @Override
    public Object getBean(String name) {

        // 查询 bean 缓存
        Object singleton = getSingleton(name);
        if (singleton != null) {
            return singleton;
        }


        // 获取配置
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

            // bean 初始化
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
    public void registerBeanDefinition(BeanDefinition beanDefinition, String name) {
        beanMap.put(name, beanDefinition);
    }

    @Override
    public void close() {
        destroySingletons();
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
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

    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {

        // 前置处理
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object current = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
            if(current != null){
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
            if(current != null){
                bean = current;
            }
        }

        return bean;

    }

}
