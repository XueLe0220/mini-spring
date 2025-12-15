package cn.xuele.minispring.aop.framework;

import java.lang.reflect.Method;


/**
 * 方法调用描述符
 * 封装了目标对象、方法对象、参数等运行时信息
 *
 * @author XueLe
 */
public interface MethodInvocation {
    /**
     * 继续处理调用链中的下一个拦截器或目标方法
     * @return 方法返回值
     * @throws Throwable 执行异常
     */
    Object proceed() throws Throwable;

    Method getMethod();

    Object[] getArguments();

    Object getThis();
}