package cn.xuele.minispring.beans.factory.support;


import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.beans.factory.BeanFactoryAware;
import cn.xuele.minispring.beans.factory.ConfigurableListableBeanFactory;
import cn.xuele.minispring.beans.factory.config.BeanDefinition;
import cn.xuele.minispring.beans.factory.config.BeanReference;
import cn.xuele.minispring.beans.factory.DisposableBean;
import cn.xuele.minispring.beans.factory.InitializingBean;
import cn.xuele.minispring.beans.PropertyValue;
import cn.xuele.minispring.beans.factory.config.BeanPostProcessor;
import cn.xuele.minispring.beans.factory.config.DisposableBeanAdapter;
import cn.xuele.minispring.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
public class DefaultListableBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {


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

        return createBean(name, beanDefinition);

    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {

        Object bean = null;

        try {

            // 1. 实例化
            Class<?> beanClass = beanDefinition.getBeanClass();
            bean = beanClass.getConstructor().newInstance();

            // 2. 如果是单例,提前暴露给工厂
            if (beanDefinition.isSingleton()) {
                Object finalBean = bean;
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, finalBean));
            }

            // 3. 属性填充
            applyPropertyValues(bean, beanDefinition);

            // 4. bean 初始化 在这一步完成对实例化 bean 的后置处理
            bean = initializeBean(beanName, bean, beanDefinition);

        } catch (Exception e) {
            throw new BeansException("Failed to instantiate bean '" + beanName + "'", e);
        }

        // 5. 注册有销毁方法的 Bean
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        // 6. 如果是单例，添加到一级缓存
        if (beanDefinition.isSingleton()) {
            Object exposedObject = getSingleton(beanName);
            if (exposedObject != null) {
                bean = exposedObject;
            }
            addSingleton(beanName, bean);
        }

        return bean;
    }

    private Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                exposedObject =
                        ((InstantiationAwareBeanPostProcessor) beanPostProcessor).getEarlyBeanReference(exposedObject
                                , beanName);
                if (exposedObject == null) return null;
            }
        }
        return exposedObject;
    }

    private void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 1. bean 实现 Disposable 接口 || 配置了 destroy-method
        if (bean instanceof DisposableBean || beanDefinition.getDestroyMethodName() != null && !beanDefinition.getDestroyMethodName().isEmpty()) {
            // 包装成统一的 adapter 添加进 DisposableList
            addDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
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
     * @param bean           - 已实例化的 Bean 对象
     * @param beanDefinition - Bean 的定义信息
     * @throws Exception - 反射操作异常
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
     * @return 初始化后的 bean
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
        try {
            invokeInitMethods(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Invocation of init method of bean[" + beanName + "] failed", e);
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

    /**
     * 执行初始化方法
     * 优先级：InitializingBean 接口 > XML init-method
     */
    private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        // 1. 实现接口的方式
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }

        // 2. XML 配置的方式
        String initMethodName = beanDefinition.getInitMethodName();
        // 判断不为空，且避免和接口方法重名（防止执行两次）
        if (initMethodName != null && !initMethodName.isEmpty()) {
            // 如果配置的方法名和 InitializingBean 的方法名一样，且已经执行过了，就跳过
            if (bean instanceof InitializingBean && "afterPropertiesSet".equals(initMethodName)) {
                return;
            }

            // 反射调用
            Method initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
            if (initMethod == null) {
                throw new BeansException("Could not find an init method named '" + initMethodName + "' on bean with " +
                        "name '" + beanName + "'");
            }
            initMethod.invoke(bean);
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeansException("No bean named '" + beanName + "' is defined");
        }
        return beanDefinition;
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {
        for (String beanName : beanMap.keySet()) {
            BeanDefinition beanDefinition = beanMap.get(beanName);
            if (beanDefinition.isSingleton()) {
                getBean(beanName);
            }
        }
    }
}
