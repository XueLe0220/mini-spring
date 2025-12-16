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

    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";

    /** bean 类型 */
    private Class<?> beanClass;

    /** 属性列表 */
    private List<PropertyValue> propertyValueList = new ArrayList<>();

    /** bean 作用域，默认为单例 */
    private String scope = SCOPE_SINGLETON;

    /** 初始化方法 */
    private String initMethodName;

    /** destroy 方法 */
    private String destroyMethodName;

    private boolean singleton = true;

    private boolean prototype = false;

    public BeanDefinition(Class<?> beanClass){
        this.beanClass = beanClass;
    }

    public void addProperty(PropertyValue propertyValue){
        propertyValueList.add(propertyValue);
    }

    public void setScope(String scope){
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }

}
