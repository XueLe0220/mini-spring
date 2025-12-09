import cn.xuele.minispring.beans.PropertyValue;
import cn.xuele.minispring.beans.factory.BeanFactory;
import cn.xuele.minispring.beans.factory.config.BeanDefinition;
import cn.xuele.minispring.beans.factory.config.BeanReference;
import cn.xuele.minispring.beans.factory.support.DefaultListableBeanFactory;
import cn.xuele.test.bean.OrderService;
import cn.xuele.test.bean.UserService;
import cn.xuele.test.common.MyBeanPostProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IoC 容器核心流程测试
 *
 * @author XueLe
 */
public class IoCServiceTest {
    @Test
    public void test_BeanFactory_RegisterAndGet() {
        // 1. Setup (准备)
        BeanFactory beanFactory = new DefaultListableBeanFactory();
        BeanDefinition bd = new BeanDefinition(UserService.class);
        bd.addProperty(new PropertyValue("userName", "UserA"));

        // 2. Execute (执行)
        beanFactory.registerBeanDefinition("userService", bd);
        UserService userService = (UserService) beanFactory.getBean("userService");

        // 3. Assert (断言 - 机器自动检查)
        // 检查对象是否创建成功
        assertNotNull(userService, "Bean should not be null");
        // 检查属性是否注入成功 (需要在 UserService 里把 queryUserInfo 改为返回字符串)
        String result = userService.queryUserInfo();
        assertEquals("查询用户:UserA", result, "Return value verification failed");
    }

    @Test
    public void test_BeanFactory_Singleton() {
        // 1. Setup
        BeanFactory beanFactory = new DefaultListableBeanFactory();
        BeanDefinition bd = new BeanDefinition(UserService.class);
        bd.addProperty(new PropertyValue("userName", "UserA"));

        // 2. Execute
        beanFactory.registerBeanDefinition("userService", bd);
        UserService u1 = (UserService) beanFactory.getBean("userService");
        UserService u2 = (UserService) beanFactory.getBean("userService");

        // 3. Assert
        assertSame(u1, u2);
    }


    @Test
    public void test_BeanFactory_BeanReference() {
        // 1. Setup
        BeanFactory beanFactory = new DefaultListableBeanFactory();
        // 准备userService
        BeanDefinition userBd = new BeanDefinition(UserService.class);
        userBd.addProperty(new PropertyValue("userName", "UserA"));
        // 准备orderService
        BeanDefinition orderBd = new BeanDefinition(OrderService.class);
        orderBd.addProperty(new PropertyValue("shopName", "金城大王小卖部"));
        orderBd.addProperty(new PropertyValue("userService", new BeanReference("userService")));

        // 2. Execute
        beanFactory.registerBeanDefinition("userService", userBd);
        beanFactory.registerBeanDefinition("orderService", orderBd);
        OrderService orderService = (OrderService) beanFactory.getBean("orderService");

        // 3. Assert
        assertNotNull(orderService);
        assertNotNull(orderService.getUserService());
    }

    @Test
    public void test_Bean_Lifecycle() {
        // 1. Setup
        BeanFactory beanFactory = new DefaultListableBeanFactory();
        BeanDefinition userBd = new BeanDefinition(UserService.class);
        userBd.addProperty(new PropertyValue("userName", "UserA"));

        // 2. Execute
        beanFactory.registerBeanDefinition("userService", userBd);
        UserService userService = (UserService) beanFactory.getBean("userService");

        // 3. Assert
        // 验证初始化方法
        String statusAfterInit = userService.getStatusRecorder().toString();
        assertEquals("init_done", statusAfterInit, "Bean 应该在获取时自动执行初始化方法");
        // 验证销毁方法
        beanFactory.close();
        String statusAfterClose = userService.getStatusRecorder().toString();
        assertEquals("init_done_destroy_done", statusAfterClose, "容器关闭时应该自动执行销毁方法");
    }


    @Test
    public void test_Bean_Processor(){
        // 1. Setup
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        BeanDefinition userBd = new BeanDefinition(UserService.class);
        userBd.addProperty(new PropertyValue("userName", "UserA"));

        // 2. Execute
        beanFactory.registerBeanDefinition("userService", userBd);
        beanFactory.addBeanPostProcessor(new MyBeanPostProcessor());
        UserService userService = (UserService) beanFactory.getBean("userService");

        // 3. Assert
        assertTrue(userService.isBeforeInitializationFlag());
        assertTrue(userService.isAfterInitializationFlag());
    }
}