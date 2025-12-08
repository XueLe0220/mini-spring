package cn.xuele.minispring.beans.factory.config;

import lombok.Getter;

/**
 * @author XueLe
 */
@Getter
public class BeanReference {
    private String beanName;

    public BeanReference(String beanName) {
        this.beanName = beanName;
    }
}
