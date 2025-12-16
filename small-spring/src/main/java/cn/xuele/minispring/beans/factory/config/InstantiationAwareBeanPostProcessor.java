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


}