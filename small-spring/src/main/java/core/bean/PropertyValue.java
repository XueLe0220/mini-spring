package core.bean;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 属性值定义
 * <p>
 * 用于封装 Bean 的属性注入信息。
 * 包含属性名称（name）和具体的值（value）。
 *
 * @author XueLe
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyValue {
    private String name;
    private Object value;
}
