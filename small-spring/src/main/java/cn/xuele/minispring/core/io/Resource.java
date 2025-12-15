package cn.xuele.minispring.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 *
 * @author XueLe
 */
public interface Resource {

    /**
     * 获取当前资源的输入流
     */
    InputStream getInputStream() throws IOException;
}
