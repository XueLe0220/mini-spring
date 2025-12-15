package cn.xuele.minispring.beans.factory;

import cn.xuele.minispring.beans.BeansException;

/**
 * 实现该接口的 Bean 可以感知到所属的 BeanFactory
 * @author XueLe
 */
public interface BeanFactoryAware extends Aware {

    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}