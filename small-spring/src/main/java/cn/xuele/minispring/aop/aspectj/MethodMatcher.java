package cn.xuele.minispring.aop.aspectj;

import java.lang.reflect.Method;

/**
 * 切点的一部分：方法匹配器
 * 用于判断切点是否适用于给定的方法
 * @author XueLe
 */
public interface MethodMatcher {
    /**
     * 切点是否匹配该方法？
     * @param method 目标方法
     * @param targetClass 目标类
     * @return true 匹配
     */
    boolean matches(Method method, Class<?> targetClass);
}