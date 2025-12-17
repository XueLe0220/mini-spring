package cn.xuele.minispring.beans.factory.config;

/**
 * @author XueLe
 * @since 2025/12/17
 */
public interface ObjectFactory<T> {
    T getObject();
}
