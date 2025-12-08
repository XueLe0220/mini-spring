/**
 * @author XueLe
 * @since 2025/12/7
 */
public class OrderService {
    private String shopName;
    private UserService userService;

    // 必须要有测试方法来看看注入是否成功
    public void fakeService() {
        System.out.println("订单店铺: " + shopName);
        // 如果注入失败，这里会报空指针；如果成功，会打印 UserService 的信息
        System.out.println("依赖服务验证: " + userService);
        if (userService != null) {
            userService.fakeService();
        }
    }
}
