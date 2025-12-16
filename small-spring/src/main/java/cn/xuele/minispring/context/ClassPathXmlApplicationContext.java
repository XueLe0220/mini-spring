package cn.xuele.minispring.context;

import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.beans.factory.ConfigurableListableBeanFactory;
import cn.xuele.minispring.beans.factory.support.DefaultListableBeanFactory;
import cn.xuele.minispring.beans.factory.support.XmlBeanDefinitionReader;

/**
 * @author XueLe
 * @since 2025/12/16
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    private String[] configLocations;

    // 核心工厂对象
    private DefaultListableBeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String configLocations) throws BeansException {
        this(new String[]{configLocations});
    }

    public ClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        this.configLocations = configLocations;
        refresh();
    }

    @Override
    protected void refreshBeanFactory() {

        // 1. 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 创建读取器
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory, this);

        // 3. 加载配置文件
        if (configLocations != null) {
            reader.loadBeanDefinitions(configLocations);
        }

        // 4. 保存 factory 引用，供 getBeanFactory() 使用
        this.beanFactory = beanFactory;
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }


}
