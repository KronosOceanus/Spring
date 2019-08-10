package entity2;

import java.io.Serializable;
import java.util.List;

//二级缓存实现该接口，并在 RoleMapper2 中配置缓存
public class Role2 implements Serializable {

    public static final long serialVersionUID = 598736524547906734L;

    private int id;
    private String roleName;
    private String note;
    //关联用户信息
    private List<User2> user2s;






    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<User2> getUser2s() {
        return user2s;
    }

    public void setUser2s(List<User2> user2s) {
        this.user2s = user2s;
    }
}
