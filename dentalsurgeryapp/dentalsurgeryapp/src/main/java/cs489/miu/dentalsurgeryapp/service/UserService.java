package cs489.miu.dentalsurgeryapp.service;

import cs489.miu.dentalsurgeryapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    User saveUser(User user);
    User updateUser(User user);
    User updateUserProfile(Integer userId, String firstName, String lastName, String username, String email, boolean enabled, List<String> roleNames);
    void deleteUser(Integer id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}