package cs489.miu.dentalsurgeryapp.service;

import cs489.miu.dentalsurgeryapp.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(Integer id);
    Optional<Role> getRoleByName(String name);
    Role saveRole(Role role);
    void deleteRole(Integer id);
}