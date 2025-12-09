package cn.xuele.test.bean;

import lombok.Getter;

/**
 * @author XueLe
 * @since 2025/12/7
 */
public class OrderService {
    private String shopName;
    @Getter
    private UserService userService;

    // 必须要有测试方法来看看注入是否成功
    public String queryShopInfo(){
        return "商铺名称: " + shopName;
    }

}
