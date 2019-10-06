package pojo;

import java.io.Serializable;

//必须可序列化
public class Role implements Serializable {
    //19 位置
    private static final long serialVersionUID = 1122334455667788995L;

    private int id;
    private String roleName;
    private String note;


    @Override
    public String toString() {
        return "id=" + id + ", roleName=" + roleName + ", note=" + note;
    }

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
}
