package cn.xuele.minispring.beans.factory;

import java.util.Map;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public interface ListableBeanFactory extends BeanFactory{

    /**
     * 根据类型获取所有的 Bean 实例
     *
     * @param type 目标类型
     * @return Map<BeanName, BeanInstance>
     */
    <T> Map<String, T> getBeansOfType(Class<T> type);
}
