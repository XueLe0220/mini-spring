package cn.xuele.minispring.aop.framework.autoproxy;

import cn.xuele.minispring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import cn.xuele.minispring.aop.Advisor;
import cn.xuele.minispring.aop.Advice;
import cn.xuele.minispring.aop.Pointcut;
import cn.xuele.minispring.aop.framework.CglibAopProxy;
import cn.xuele.minispring.aop.framework.MethodInterceptor;
import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.beans.factory.BeanFactory;
import cn.xuele.minispring.beans.factory.BeanFactoryAware;
import cn.xuele.minispring.beans.factory.ListableBeanFactory;
import cn.xuele.minispring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import cn.xuele.minispring.beans.factory.support.DefaultListableBeanFactory;

import java.util.Collection;
import java.util.Map;

/**
 * 自动代理创建器
 * 核心逻辑：扫描 Advisor -> 匹配 -> 移花接木
 *
 * @author XueLe
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private ListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        // 1. 拦截基础设施 (如果 bean 为 Advice PointCut Advisor 直接返回 不能被代理)
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }

        // 2. 获取所有切面
        Map<String, AspectJExpressionPointcutAdvisor> advisorMap =
                beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class);

        // 3. 切点匹配
        Collection<AspectJExpressionPointcutAdvisor> advisors = advisorMap.values();
        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            // 3.1 类过滤
            if (advisor.getPointcut().getClassFilter().matches(bean.getClass())) {
                // 命中直接创建代理对象
                return new CglibAopProxy(bean,
                        (MethodInterceptor) advisor.getAdvice(),
                        advisor.getPointcut().getMethodMatcher())
                        .getProxy();
            }

        }
        // 不匹配，原路返回
        return bean;
    }

    // 判断 bean 是否为Advice PointCut Advisor
    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass)
                || Pointcut.class.isAssignableFrom(beanClass)
                || Advisor.class.isAssignableFrom(beanClass);
    }

}