package cn.xuele.minispring.context;

import cn.xuele.minispring.beans.BeansException;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public interface ConfigurableApplicationContext extends ApplicationContext{
    /**
     * 核心方法：刷新容器
     * 1. 加载 Bean 定义
     * 2. 注册 BeanPostProcessor
     * 3. 初始化所有单例 Bean
     *
     */
    void refresh() throws BeansException;

    /**
     * 关闭容器
     * 负责触发单例 Bean 的销毁方法
     */
    void close();

    /**
     * 向 JVM 注册关闭钩子
     * 确保当虚拟机关闭时，容器能优雅地关闭（自动调用 close）
     */
    void registerShutdownHook();
}
