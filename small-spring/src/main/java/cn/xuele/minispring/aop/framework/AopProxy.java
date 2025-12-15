package cn.xuele.minispring.aop.framework;

/**
 * AOP 代理的顶层抽象接口
 * 定义获取代理对象的标准方法
 *
 * @author XueLe
 */
public interface AopProxy {
    Object getProxy();
}