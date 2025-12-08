package cn.xuele.test.common;

import cn.xuele.minispring.beans.factory.config.BeanPostProcessor;

/**
 * @author XueLe
 * @since 2025/12/8
 */
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if ("userService".equals(beanName)) {
            System.out.println(">> [BPP] 在初始化之前，我把 cn.xuele.test.bean.UserService 拦截下来了！");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ("userService".equals(beanName)) {
            System.out.println(">> [BPP] 在初始化之后，我又拦截了一次！");
        }
        return bean;
    }
}