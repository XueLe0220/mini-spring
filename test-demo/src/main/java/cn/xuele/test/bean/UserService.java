package cn.xuele.test.bean;

import cn.xuele.minispring.beans.factory.DisposableBean;
import cn.xuele.minispring.beans.factory.InitializingBean;

/**
 * @author XueLe
 * @since 2025/12/7
 */
public class UserService implements InitializingBean, DisposableBean {

    private String userName;

    public void fakeService() {
        System.out.println("测试方法" + userName);
    }

    @Override
    public void destroy() {
        System.out.println("销毁" + userName);
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println(userName + "连接数据库");
    }
}
