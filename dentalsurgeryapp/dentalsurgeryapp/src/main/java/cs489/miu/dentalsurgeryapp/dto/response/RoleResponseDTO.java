package cs489.miu.dentalsurgeryapp.dto.response;

public class RoleResponseDTO {
    
    private Integer roleId;
    private String name;

    public RoleResponseDTO() {
    }

    public RoleResponseDTO(Integer roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}