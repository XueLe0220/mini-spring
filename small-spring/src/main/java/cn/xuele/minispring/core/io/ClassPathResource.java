package cn.xuele.minispring.core.io;

import cn.hutool.core.util.ClassUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public class ClassPathResource implements Resource {

    private final String path;
    private ClassLoader classLoader;

    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    public ClassPathResource(String path, ClassLoader classLoader) {
        this.path = path;
        this.classLoader = classLoader != null ? classLoader : ClassUtil.getClassLoader();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = classLoader.getResourceAsStream(path);
        if (is == null) {
            throw new IOException(this.path + " cannot be opened because it does not exist");
        }
        return is;
    }
}
