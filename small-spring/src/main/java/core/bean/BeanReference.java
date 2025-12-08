package core.bean;

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
