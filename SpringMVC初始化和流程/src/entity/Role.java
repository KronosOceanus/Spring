package entity;

public class Role {

    private Integer id;
    private String roleName;
    private String note;

    public Role(){}
    public Role(Integer id, String roleName, String note){
        this.id = id;
        this.roleName = roleName;
        this.note = note;
    }

    @Override
    public String toString() {
        return "Role[id = " + id + ", roleName = " + roleName + ", " +
                "note = " + note + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
