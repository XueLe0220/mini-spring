package cn.xuele.minispring.core.io;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public interface ResourceLoader {

    /**
     * 判断是否从 Classpath 加载的前缀
     */
    String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String location);
}
