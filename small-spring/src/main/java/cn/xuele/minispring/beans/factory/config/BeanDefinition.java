package cn.xuele.minispring.beans.factory.config;

import cn.xuele.minispring.beans.PropertyValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean 定义信息
 * <p>
 * 用于描述一个 Bean 的实例，包括其类型、属性值等元数据。
 * 在 Spring 中，这是从配置（XML/注解）到对象实例之间的桥梁。
 *
 * @author XueLe
 */
@Getter
@Setter
@NoArgsConstructor
public class BeanDefinition {
    private Class<?> beanClass;
    // 防止空指针
    private List<PropertyValue> propertyValueList = new ArrayList<>();

    public BeanDefinition(Class<?> beanClass){
        this.beanClass = beanClass;
    }

    public void addProperty(PropertyValue propertyValue){
        propertyValueList.add(propertyValue);
    }
}
