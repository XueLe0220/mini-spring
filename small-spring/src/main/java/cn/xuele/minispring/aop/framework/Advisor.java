package cn.xuele.minispring.aop.framework;

/**
 * Advisor 访问者接口
 * 它是 AOP 的顶层抽象，用来管理 Advice
 * @author XueLe
 */
public interface Advisor {
    /**
     * 获取增强逻辑
     */
    Advice getAdvice();
}