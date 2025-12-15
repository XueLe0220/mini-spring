import cn.xuele.minispring.aop.aspectj.AspectJExpressionPointcut;
import cn.xuele.minispring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import cn.xuele.minispring.aop.aspectj.ClassFilter;
import cn.xuele.minispring.aop.aspectj.MethodMatcher;
import cn.xuele.minispring.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import cn.xuele.minispring.beans.PropertyValue;
import cn.xuele.minispring.beans.factory.BeanFactory;
import cn.xuele.minispring.beans.factory.config.BeanDefinition;
import cn.xuele.minispring.beans.factory.config.BeanReference;
import cn.xuele.minispring.beans.factory.support.DefaultListableBeanFactory;
import cn.xuele.minispring.beans.factory.support.DefaultSingletonBeanRegistry;
import cn.xuele.minispring.core.io.ClassPathResource;
import cn.xuele.minispring.core.io.DefaultResourceLoader;
import cn.xuele.minispring.core.io.FileSystemResource;
import cn.xuele.minispring.core.io.Resource;
import cn.xuele.minispring.core.io.ResourceLoader;
import cn.xuele.minispring.core.io.UrlResource;
import cn.xuele.test.bean.OrderService;
import cn.xuele.test.bean.UserService;
import cn.xuele.test.common.MyBeanPostProcessor;
import cn.xuele.test.common.UserServiceInterceptor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

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
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
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
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
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
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
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
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
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
    public void test_Bean_Processor() {
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

    @Test
    public void test_Pointcut_Execution() {
        // 1.Setup
        String execution = "execution(* cn.xuele.test.bean.UserService.*(..))";
        AspectJExpressionPointcut aep = new AspectJExpressionPointcut(execution);
        Class<UserService> userServiceClass = UserService.class;
        Method[] methods = userServiceClass.getMethods();

        // 2. Execute
        boolean classMatches = aep.matches(userServiceClass);
        boolean methodMatches = aep.matches(methods[0], userServiceClass);

        // 3. Assert
        assertTrue(classMatches);
        assertTrue(methodMatches);

    }

    @Test
    public void test_advisor() throws Exception {
        // 1. Setup: 创建目标对象
        UserService userService = new UserService();

        // 2. Setup: 配置 Advisor (切面)
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        // 2.1 设置规则
        advisor.setExpression("execution(* cn.xuele.test.bean.UserService.queryUserInfo(..))");
        // 2.2 设置逻辑 (拦截器)
        advisor.setAdvice(new UserServiceInterceptor());

        // 3. Execute: 模拟容器检查流程
        // 3.1 从 Advisor 拿出 Pointcut
        ClassFilter classFilter = advisor.getPointcut().getClassFilter();

        // 3.2 匹配类
        if (classFilter.matches(userService.getClass())) {
            // 3.3 匹配方法
            MethodMatcher methodMatcher = advisor.getPointcut().getMethodMatcher();
            Method method1 = userService.getClass().getMethod("queryUserInfo");
            Method method2 = userService.getClass().getMethod("destroy");

            boolean matches = methodMatcher.matches(method1, userService.getClass());
            boolean noMatches = methodMatcher.matches(method2, userService.getClass());

            // 4. Assert: 验证是否匹配成功
            assertTrue(matches, "Advisor 里的 Pointcut 应该能匹配到 queryUserInfo 方法");
            assertFalse(noMatches, "Advisor 里的 Pointcut 不应该能匹配到 destroy 方法");
        } else {
            fail("ClassFilter 应该匹配 UserService");
        }
    }

    @Test
    public void test_auto_proxy() throws Exception {
        // 1. Setup
        // 1.1 初始化容器
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 1.2 注册业务 bean
        BeanDefinition userBD = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", userBD);
        // 1.3 配置切面 (Advisor)
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setAdvice(new UserServiceInterceptor());
        advisor.setExpression("execution(* cn.xuele.test.bean.UserService.queryUserInfo(..))");
        // 1.4 注册切面到容器
        beanFactory.registerBeanDefinition("advisor", new BeanDefinition(AspectJExpressionPointcutAdvisor.class));
        //  简化注册，直接把准备好的 advisor 实例塞进单例池
        Method addSingleton = DefaultSingletonBeanRegistry.class.getDeclaredMethod("addSingleton", String.class, Object.class);
        addSingleton.setAccessible(true);
        addSingleton.invoke(beanFactory, "advisor", advisor);
        // 1.5 注册自动代理器
        beanFactory.registerBeanDefinition("autoProxyCreator", new BeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        // 1.6 容器创建自动代理器 bean
        DefaultAdvisorAutoProxyCreator autoProxyCreator = (DefaultAdvisorAutoProxyCreator) beanFactory.getBean("autoProxyCreator");
        // 1.7 手动将自动代理器添加进容器 BPP 列表
        beanFactory.addBeanPostProcessor(autoProxyCreator);

        // 2. Execute
        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.destroy();
        assertFalse(userService.isInterceptorFlag(), "错误拦截");
        userService.queryUserInfo();

        // 3. Assert
        assertTrue(userService.isInterceptorFlag(), "发生错误 未被拦截");
    }

    @Test
    public void test_resource_loader(){
        // 1. Setup
        // 1.1 初始化资源加载器
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        // 1.2 准备资源
        String classpathLocation = "classpath:test.txt";
        String fileSystemLocation = "E:\\Code\\Code4Java\\mini-spring\\test-demo\\src\\main\\resources\\test.txt";

        // 2. Execute
        Resource classpathResource = resourceLoader.getResource(classpathLocation);
        Resource fileSystemResource = resourceLoader.getResource(fileSystemLocation);

        // 3. Assert
        assertInstanceOf(ClassPathResource.class, classpathResource);
        assertInstanceOf(FileSystemResource.class, fileSystemResource);
        assertNotEquals(UrlResource.class, fileSystemResource.getClass());
    }
}