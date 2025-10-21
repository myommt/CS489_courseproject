package cs489.miu.dentalsurgeryapp.service.impl;

import cs489.miu.dentalsurgeryapp.model.User;
import cs489.miu.dentalsurgeryapp.repository.UserRepository;
import cs489.miu.dentalsurgeryapp.service.UserService;
import cs489.miu.dentalsurgeryapp.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleService roleService;
    
    public UserServiceImpl(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User updateUserProfile(Integer userId, String firstName, String lastName, String username, String email, boolean enabled, List<String> roleNames) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            
            // Update only the profile fields, leave password unchanged
            existingUser.setFirstName(firstName);
            existingUser.setLastName(lastName);
            existingUser.setUsername(username);
            existingUser.setEmail(email);
            existingUser.setEnabled(enabled);
            
            // Update roles if provided
            if (roleNames != null && !roleNames.isEmpty()) {
                var selectedRoles = roleService.getAllRoles().stream()
                    .filter(role -> roleNames.contains(role.getName()))
                    .toList();
                existingUser.getRoles().clear();
                existingUser.getRoles().addAll(selectedRoles);
            }
            
            return userRepository.save(existingUser);
        }
        return null; // User not found
    }
    
    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}