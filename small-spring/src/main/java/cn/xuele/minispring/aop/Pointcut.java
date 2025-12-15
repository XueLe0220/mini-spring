package cn.xuele.minispring.aop;

import cn.xuele.minispring.aop.aspectj.ClassFilter;
import cn.xuele.minispring.aop.aspectj.MethodMatcher;

/**
 * AOP 切点顶级接口
 * 组合了 ClassFilter 和 MethodMatcher
 * @author XueLe
 */
public interface Pointcut {

    /**
     * 获取类过滤器
     */
    ClassFilter getClassFilter();

    /**
     * 获取方法匹配器
     */
    MethodMatcher getMethodMatcher();
}
