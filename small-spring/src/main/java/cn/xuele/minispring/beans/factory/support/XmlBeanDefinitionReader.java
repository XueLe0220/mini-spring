package cn.xuele.minispring.beans.factory.support;

import cn.xuele.minispring.beans.BeansException;
import cn.xuele.minispring.beans.PropertyValue;
import cn.xuele.minispring.beans.factory.config.BeanDefinition;
import cn.xuele.minispring.beans.factory.config.BeanReference;
import cn.xuele.minispring.core.io.Resource;
import cn.xuele.minispring.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(Resource resource) throws BeansException {
        try (InputStream inputStream = resource.getInputStream()) {
            doLoadBeanDefinitions(inputStream);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadBeanDefinitions(Resource... resources) throws BeansException {
        for (Resource resource : resources) {
            loadBeanDefinitions(resource);
        }
    }

    @Override
    public void loadBeanDefinitions(String location) throws BeansException {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        loadBeanDefinitions(resource);
    }

    @Override
    public void loadBeanDefinitions(String... locations) throws BeansException {
        for (String location : locations) {
            loadBeanDefinitions(location);
        }
    }

    /**
     * 核心解析逻辑
     */
    protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException {
        // 1. 加载 XML 文档对象
        Document doc = doLoadDocument(inputStream);
        Element root = doc.getDocumentElement();
        NodeList childNodes = root.getChildNodes();

        // 2. 遍历根节点下的所有子节点 (查找 <bean> 标签)
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            // 过滤：必须是元素节点，且标签名为 bean
            if (!(node instanceof Element)) continue;
            if (!"bean".equals(node.getNodeName())) continue;

            // 3. 解析 <bean> 标签的基本信息
            Element bean = (Element) node;
            String id = bean.getAttribute("id");
            String name = bean.getAttribute("name");
            String className = bean.getAttribute("class");

            // 加载类对象
            Class<?> clazz = Class.forName(className);

            // 确定 beanName (优先级：id > name > 类名首字母小写)
            String beanName = id.isEmpty() ? name : id;
            if (beanName.isEmpty()) {
                // 如果也没 name，就用类名 (如 UserService -> userService)
                beanName = Character.toLowerCase(className.charAt(0)) + className.substring(1);
            }

            // 4. 创建 BeanDefinition 对象
            BeanDefinition beanDefinition = new BeanDefinition(clazz);

            // 5. 解析 <bean> 下的 <property> 标签 (属性填充)
            NodeList propertyNodes = bean.getChildNodes();
            for (int j = 0; j < propertyNodes.getLength(); j++) {
                Node propertyNode = propertyNodes.item(j);
                if (!(propertyNode instanceof Element)) continue;
                if (!"property".equals(propertyNode.getNodeName())) continue;

                // 解析 property 的 name, value, ref
                Element property = (Element) propertyNode;
                String attrName = property.getAttribute("name");
                String attrValue = property.getAttribute("value");
                String attrRef = property.getAttribute("ref");

                // 获取属性值：是值类型还是引用类型？
                Object value = attrValue;
                if (!attrRef.isEmpty()) {
                    // 如果 ref 不为空，说明是引用，包装成 BeanReference
                    value = new BeanReference(attrRef);
                }

                // 添加到 BeanDefinition 的属性集合中
                PropertyValue propertyValue = new PropertyValue(attrName, value);
                beanDefinition.getPropertyValueList().add(propertyValue);
            }

            // 6. 注册 BeanDefinition (判重逻辑可选)
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
            }
            // 注册到 Registry (即 DefaultListableBeanFactory)
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }

    /**
     * 辅助方法：处理 XML 解析的样板代码
     */
    private Document doLoadDocument(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        } catch (Exception e) {
            throw new BeansException("Error loading XML document", e);
        }
    }
}
