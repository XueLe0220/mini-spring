package cn.xuele.minispring.core.io;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 默认资源加载器
 *
 * @author XueLe
 */
public class DefaultResourceLoader implements ResourceLoader {

    @Override
    public Resource getResource(String location) {
        // 1.处理前缀
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location);
        } else {
            // 2.尝试当成 url 处理
            try {
                URL url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException e) {
                // 3.如果不是 url 说明应该是一个系统文件路径
                return new FileSystemResource(location);
            }
        }
    }
}
