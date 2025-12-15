package cn.xuele.minispring.beans.factory;

/**
 * 标记接口。<br>
 * 实现该接口的 Bean 会在创建后被容器识别，并调用其对应的 {@code setXxx(Xxx xxx)} 方法，
 * 把容器自身的某些核心对象（如 {@link cn.xuele.minispring.beans.factory.BeanFactory}）
 * 注入到 Bean 中，供 Bean 在运行时与容器交互。
 *
 * @author XueLe
 */
public interface Aware {
}