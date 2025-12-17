package cn.xuele.minispring.beans.factory.config;

import cn.xuele.minispring.beans.BeansException;

/**
 * BeanPostProcessor 的子接口
 *
 * @author XueLe
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 在 Bean 实例化之前执行 (本次暂不实现逻辑，留个空位)
     */
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    /**
     * 提前暴露 Bean 引用
     * 用于解决循环依赖时，提前生成代理对象
     * * @param bean 原始对象
     * @param beanName bean名称
     * @return 可能是原始对象，也可能是代理对象
     */
    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }


}