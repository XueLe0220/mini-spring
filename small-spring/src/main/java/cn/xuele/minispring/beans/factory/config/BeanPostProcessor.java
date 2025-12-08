package cn.xuele.minispring.beans.factory.config;

/**
 * @author XueLe
 * @since 2025/12/8
 */
public interface BeanPostProcessor {
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
