package cn.xuele.minispring.aop;

/**
 * 带有切点的 Advisor
 * 绝大多数 AOP 切面都通过此接口管理
 * @author XueLe
 */
public interface PointcutAdvisor extends Advisor {

    /**
     * 获取切点（筛选规则）
     */
    Pointcut getPointcut();
}