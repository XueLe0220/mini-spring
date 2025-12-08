import core.bean.BeanDefinition;
import core.bean.BeanReference;
import core.bean.PropertyValue;
import core.factory.BeanFactory;
import core.factory.DefaultListableBeanFactory;
import org.junit.jupiter.api.Test;


/**
 * IoC 容器核心流程测试
 *
 * @author XueLe
 */
public class IocTest {

    @Test
    public void testIoc() {
        // 1. 初始化工厂
        BeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 准备 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClass(UserService.class);

        // 3. 设置属性
        PropertyValue propertyValue = new PropertyValue("userName", "小王");
        beanDefinition.addProperty(propertyValue);

        // 4. 注册
        beanFactory.registerBeanDefinition(beanDefinition, "userService");

        // 5. 获取并验证 (不再需要 try-catch，因为 BeansException 是运行时异常，抛出即代表测试失败)
        // 使用 assertDoesNotThrow 是一种防御性写法，或者直接调用也可以
        UserService userService = (UserService) beanFactory.getBean("userService");

        // 6. 验证结果
        userService.fakeService();
    }

    @Test
    public void testDI() {

        // 初始化 bean 工厂
        BeanFactory beanFactory = new DefaultListableBeanFactory();

        // 1. 注册 userService
        BeanDefinition userBD = new BeanDefinition(UserService.class);
        userBD.addProperty(new PropertyValue("userName", "Jack"));
        beanFactory.registerBeanDefinition(userBD, "userService");

        // 2. 注册 orderService
        BeanDefinition orderBD = new BeanDefinition(OrderService.class);
        orderBD.addProperty(new PropertyValue("shopName", "金城大王小卖部"));
        orderBD.addProperty(new PropertyValue("userService", new BeanReference("userService")));
        beanFactory.registerBeanDefinition(orderBD, "orderService");

        // 3. 创建对象
        OrderService orderService = (OrderService) beanFactory.getBean("orderService");

        // 4. 测试
        orderService.fakeService();

    }

    @Test
    public void testSingleton() {
        BeanFactory beanFactory = new DefaultListableBeanFactory();

        BeanDefinition bd = new BeanDefinition();
        bd.setBeanClass(UserService.class);
        beanFactory.registerBeanDefinition(bd, "userService");

        // 第一次获取：应该创建并放入缓存
        Object u1 = beanFactory.getBean("userService");

        // 第二次获取：应该直接从缓存拿
        Object u2 = beanFactory.getBean("userService");

        // 验证引用地址是否相同
        System.out.println("是否单例: " + (u1 == u2)); // 必须是 true
    }

    @Test
    public void testInitialAndDestroy(){
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        BeanDefinition bd = new BeanDefinition(UserService.class);
        PropertyValue pv = new PropertyValue("userName", "lcl");
        bd.addProperty(pv);

        beanFactory.registerBeanDefinition(bd, "userService");
        beanFactory.addBeanPostProcessor(new MyBeanPostProcessor());



        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.fakeService();

        beanFactory.close();
    }


}