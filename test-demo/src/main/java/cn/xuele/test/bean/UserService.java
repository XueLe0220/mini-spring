package cn.xuele.test.bean;

import cn.xuele.minispring.beans.factory.DisposableBean;
import cn.xuele.minispring.beans.factory.InitializingBean;
import lombok.Getter;
import lombok.Setter;

/**
 * @author XueLe
 * @since 2025/12/7
 */
@Getter
@Setter
public class UserService implements InitializingBean, DisposableBean {

    private String userName;
    private final StringBuilder statusRecorder = new StringBuilder();
    private boolean beforeInitializationFlag = false;
    private boolean afterInitializationFlag = false;

    private boolean interceptorFlag = false;

    // 模拟查询用户信息
    public String queryUserInfo() {
        return "查询用户:" + userName;
    }

    // Bean 销毁方法
    @Override
    public void destroy() {
        // 记录销毁状态
        System.out.println("执行销毁方法...");
        statusRecorder.append("_destroy_done");
    }

    // Bean 初始化方法
    @Override
    public void afterPropertiesSet() {
        // 不再只打印，而是记录状态
        System.out.println("执行初始化方法...");
        System.out.println("qwq");
        statusRecorder.append("init_done");
    }


}
