package cn.xuele.minispring.aop.aspectj;

/**
 * 切点的一部分：类过滤器
 * 用于判断切点是否适用于给定的接口或目标类
 *
 * @author XueLe
 */
public interface ClassFilter {

    /**
     * 切点是否匹配该类？
     * @param clazz 目标类
     * @return true 匹配
     */
    boolean matches(Class<?> clazz);
}
