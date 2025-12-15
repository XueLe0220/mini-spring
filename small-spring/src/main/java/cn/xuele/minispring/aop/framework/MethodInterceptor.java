package cn.xuele.minispring.aop.framework;

import cn.xuele.minispring.aop.Advice;

/**
 * 方法拦截器接口
 * 用户将实现此接口来编写增强逻辑（如监控、事务）
 *
 * @author XueLe
 */
public interface MethodInterceptor extends Advice {
    /**
     * 执行拦截逻辑
     * @param invocation 方法调用信息包装
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    Object invoke(MethodInvocation invocation) throws Throwable;
}