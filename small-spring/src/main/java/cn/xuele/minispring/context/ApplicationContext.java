package cn.xuele.minispring.context;

import cn.xuele.minispring.beans.factory.ListableBeanFactory;
import cn.xuele.minispring.core.io.ResourceLoader;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public interface ApplicationContext extends ListableBeanFactory, ResourceLoader {
}
