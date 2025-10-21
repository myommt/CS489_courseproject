package cs489.miu.dentalsurgeryapp.dto.util;

import cs489.miu.dentalsurgeryapp.dto.response.RoleResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.response.UserResponseDTO;
import cs489.miu.dentalsurgeryapp.model.Role;
import cs489.miu.dentalsurgeryapp.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserDTOMapper {

    public static UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        List<RoleResponseDTO> roleDTOs = user.getRoles().stream()
                .map(UserDTOMapper::toRoleResponseDTO)
                .collect(Collectors.toList());

        return new UserResponseDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                roleDTOs
        );
    }

    public static RoleResponseDTO toRoleResponseDTO(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleResponseDTO(role.getRoleId(), role.getName());
    }

    public static List<UserResponseDTO> toUserResponseDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(UserDTOMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }
}