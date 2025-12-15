package cn.xuele.minispring.aop.aspectj;

import cn.xuele.minispring.aop.Pointcut;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

/**
 * @author XueLe
 * @since 2025/12/10
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    // AspectJ 的核心解析器，支持所有原语类型
    private static final PointcutParser POINTCUT_PARSER =
            PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();

    private final PointcutExpression pointcutExpression;

    // 解析 expression 表达式
    public AspectJExpressionPointcut(String expression) {
        this.pointcutExpression = POINTCUT_PARSER.parsePointcutExpression(expression);
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
        return shadowMatch.alwaysMatches();
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }



}
