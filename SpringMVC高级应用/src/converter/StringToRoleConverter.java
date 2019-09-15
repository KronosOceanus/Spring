package converter;

import entity.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * 自定义转换器（一对一）
 */
public class StringToRoleConverter implements Converter<String, Role> {
    @Override
    public Role convert(String s) {
        if (StringUtils.isEmpty(s)){
            return null;
        }
        if (!s.contains("-")){
            return null;
        }
        String arr[] = StringUtils.delimitedListToStringArray(s, "-");
        if (arr.length != 3){
            return null;
        }
        Role role = new Role();
        role.setId(Integer.parseInt(arr[0]));
        role.setRoleName(arr[1]);
        role.setNote(arr[2]);
        return role;
    }
}
