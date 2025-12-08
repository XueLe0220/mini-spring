package cn.xuele.minispring.beans;


/**
 * Mini-Spring 框架的根异常类
 * 用于统一处理 Bean 创建、获取、注入过程中的错误
 *
 * @author XueLe
 */
public class BeansException extends RuntimeException {

    public BeansException(String msg) {
        super(msg);
    }


    public BeansException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
