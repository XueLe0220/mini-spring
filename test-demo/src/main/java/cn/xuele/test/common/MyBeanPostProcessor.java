package cn.xuele.test.common;

import cn.xuele.minispring.beans.factory.config.BeanPostProcessor;
import cn.xuele.test.bean.UserService;
import lombok.Getter;

/**
 * @author XueLe
 * @since 2025/12/8
 */
@Getter
public class MyBeanPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if ("userService".equals(beanName)) {
            UserService userService = (UserService) bean;
            userService.setBeforeInitializationFlag(true);
            System.out.println(">> [BPP] 在初始化之前，我把 cn.xuele.test.bean.UserService 拦截下来了！");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ("userService".equals(beanName)) {
            UserService userService = (UserService) bean;
            userService.setAfterInitializationFlag(true);
            System.out.println(">> [BPP] 在初始化之后，我又拦截了一次！");
        }
        return bean;
    }
}