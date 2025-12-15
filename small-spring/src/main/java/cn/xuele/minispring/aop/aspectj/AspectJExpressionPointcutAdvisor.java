package cn.xuele.minispring.aop.aspectj;

import cn.xuele.minispring.aop.Pointcut;
import cn.xuele.minispring.aop.PointcutAdvisor;
import cn.xuele.minispring.aop.framework.Advice;
import lombok.Setter;

/**
 * @author XueLe
 * @since 2025/12/10
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    private AspectJExpressionPointcut aspectJExpressionPointcut;

    @Setter
    private Advice advice;

    private String expression;

    public void setExpression(String expression){
        aspectJExpressionPointcut = new AspectJExpressionPointcut(expression);
        this.expression = expression;
    }

    @Override
    public Pointcut getPointcut() {
        return aspectJExpressionPointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

}
