package cn.xuele.minispring.aop.framework;

import cn.xuele.minispring.aop.aspectj.MethodMatcher;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB AOP 代理的具体实现
 *
 * @author XueLe
 */
public class CglibAopProxy implements AopProxy {

    private final Object target;
    private final MethodInterceptor methodInterceptor;
    private final MethodMatcher methodMatcher;

    public CglibAopProxy(Object target, MethodInterceptor methodInterceptor, MethodMatcher methodMatcher) {
        this.target = target;
        this.methodInterceptor = methodInterceptor;
        this.methodMatcher = methodMatcher;
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        // 设置需要增强的对象
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new DynamicAdvisedInterceptor());
        return enhancer.create();
    }

    private class DynamicAdvisedInterceptor implements net.sf.cglib.proxy.MethodInterceptor {
        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

            if (methodMatcher.matches(method, target.getClass())) {
                MethodInvocation methodInvocation = new MethodInvocation() {
                    @Override
                    public Object proceed() throws Throwable {
                        return methodProxy.invoke(target, args);
                    }

                    @Override
                    public Method getMethod() {
                        return method;
                    }

                    @Override
                    public Object[] getArguments() {
                        return args;
                    }

                    @Override
                    public Object getThis() {
                        return target;
                    }
                };
                return methodInterceptor.invoke(methodInvocation);
            }
            return methodProxy.invoke(target, args);
        }
    }
}
