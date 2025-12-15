package cn.xuele.test.common;

import cn.xuele.minispring.aop.framework.MethodInterceptor;
import cn.xuele.minispring.aop.framework.MethodInvocation;
import cn.xuele.test.bean.UserService;

/**
 * @author XueLe
 * @since 2025/12/9
 */
public class UserServiceInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object target = invocation.getThis();
        if(target instanceof UserService){
            ((UserService) target).setInterceptorFlag(true);
        }

        return invocation.proceed();
    }
}