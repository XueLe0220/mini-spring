package cn.xuele.minispring.beans.factory.config;

import cn.hutool.core.util.StrUtil;
import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.beans.factory.DisposableBean;
import java.lang.reflect.Method;

/**
 * 销毁方法适配器
 * 作用：将 "普通Bean + destroy-method" 包装成标准的 DisposableBean
 * 同时也会处理实现了 DisposableBean 接口的情况
 * @author XueLe
 */
public class DisposableBeanAdapter implements DisposableBean {

    private final Object bean;
    private final String beanName;
    private final String destroyMethodName;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    @Override
    public void destroy() {
        // 1. 如果 Bean 实现了 DisposableBean 接口，先调用接口方法
        if (bean instanceof DisposableBean) {
            try {
                ((DisposableBean) bean).destroy();
            } catch (Exception e) {
                throw new BeansException("DisposableBean.destroy of bean '" + beanName + "' failed", e);
            }
        }

        // 2. 如果配置了 destroy-method，通过反射调用
        // (并且避免重复调用：如果配置的方法名就是 "destroy" 且实现了接口，就不反射调用了，因为上面已经调过了)
        if (StrUtil.isNotEmpty(destroyMethodName) &&
                !(bean instanceof DisposableBean && "destroy".equals(destroyMethodName))) {

            try {
                Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
                destroyMethod.invoke(bean);
            } catch (Exception e) {
                throw new BeansException("Invocation of destroy method '" + destroyMethodName + "' of bean '" + beanName + "' failed", e);
            }
        }
    }
}
