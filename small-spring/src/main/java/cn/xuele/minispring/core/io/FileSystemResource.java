package cn.xuele.minispring.core.io;

import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author XueLe
 * @since 2025/12/15
 */
public class FileSystemResource implements Resource {

    @Getter
    private final String path;

    private final File file;

    public FileSystemResource(String path){
        this.path = path;
        this.file = new File(path);
    }

    public FileSystemResource(File file){
        this.file = file;
        this.path = file.getPath();
    }


    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
}
