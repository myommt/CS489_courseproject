package cs489.miu.dentalsurgeryapp.controller.sysadmin.usermgt;

import cs489.miu.dentalsurgeryapp.dto.request.UserUpdateRequestDTO;
import cs489.miu.dentalsurgeryapp.model.Role;
import cs489.miu.dentalsurgeryapp.model.User;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.service.RoleService;
import cs489.miu.dentalsurgeryapp.service.UserService;
import cs489.miu.dentalsurgeryapp.service.PatientService;
import cs489.miu.dentalsurgeryapp.service.DentistService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
public class UserMgtController {
    
    // Constants
    private static final String ROLES_ATTRIBUTE = "roles";
    private static final String USER_LIST_VIEW = "secured/sysadmin/usermgt/list";
    private static final String NEW_USER_VIEW = "secured/sysadmin/usermgt/newuser";
    private static final String EDIT_USER_VIEW = "secured/sysadmin/usermgt/edituser";
    private static final String REDIRECT_USER_LIST = "redirect:/dentalsurgeryapp/secured/sysadmin/usermgt/list";
    
    private final UserService userService;
    private final RoleService roleService;
    private final PatientService patientService;
    private final DentistService dentistService;

    public UserMgtController(UserService userService, RoleService roleService,
                             PatientService patientService, DentistService dentistService) {
        this.userService = userService;
        this.roleService = roleService;
        this.patientService = patientService;
        this.dentistService = dentistService;
    }

    @GetMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/list")
    public ModelAndView displayUsersList() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("users", userService.getAllUsers());
        mav.setViewName(USER_LIST_VIEW);
        return mav;
    }

    @GetMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/new")
    public ModelAndView displayNewUserForm() {
        ModelAndView mav = new ModelAndView();
        User user = new User();
        // Set default values for UserDetails interface fields
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        mav.addObject("user", user);
        mav.addObject(ROLES_ATTRIBUTE, roleService.getAllRoles());
        // Reference lists for linking selections
        mav.addObject("patients", patientService.getAllPatients());
        mav.addObject("dentists", dentistService.getAllDentistsOrderedByName());
        mav.setViewName(NEW_USER_VIEW);
        return mav;
    }

    @PostMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/new")
    public String addNewUser(@Valid @ModelAttribute("user") User user,
                             @org.springframework.web.bind.annotation.RequestParam(value = "patientId", required = false) Integer patientId,
                             @org.springframework.web.bind.annotation.RequestParam(value = "dentistId", required = false) Integer dentistId,
                             Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("dentists", dentistService.getAllDentistsOrderedByName());
            return NEW_USER_VIEW;
        }
        
        // Check for duplicate username or email
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("usernameError", "Username already exists");
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("dentists", dentistService.getAllDentistsOrderedByName());
            return NEW_USER_VIEW;
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists");
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("dentists", dentistService.getAllDentistsOrderedByName());
            return NEW_USER_VIEW;
        }
        // Link to patient/dentist if corresponding role selected and id provided
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            boolean hasPatientRole = user.getRoles().stream().anyMatch(r -> "PATIENT".equalsIgnoreCase(r.getName()));
            boolean hasDentistRole = user.getRoles().stream().anyMatch(r -> "DENTIST".equalsIgnoreCase(r.getName()));
            if (hasPatientRole && patientId != null) {
                try { user.setPatient(patientService.getPatientById(patientId)); } catch (Exception ignored) {}
            }
            if (hasDentistRole && dentistId != null) {
                dentistService.findDentistById(dentistId).ifPresent(user::setDentist);
            }
        }
        userService.saveUser(user);
        return REDIRECT_USER_LIST;
    }

    @GetMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/edit/{userId}")
    public String editUser(@PathVariable Integer userId, Model model) {
        var userOptional = userService.getUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Convert User to UserUpdateRequestDTO
            UserUpdateRequestDTO userUpdateDTO = new UserUpdateRequestDTO();
            userUpdateDTO.setUserId(user.getUserId());
            userUpdateDTO.setFirstName(user.getFirstName());
            userUpdateDTO.setLastName(user.getLastName());
            userUpdateDTO.setUsername(user.getUsername());
            userUpdateDTO.setEmail(user.getEmail());
            userUpdateDTO.setEnabled(user.isEnabled());
            
            // Convert roles to role names
            var roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();
            userUpdateDTO.setRoleNames(roleNames);
            
            model.addAttribute("user", userUpdateDTO);
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return EDIT_USER_VIEW;
        }
        return REDIRECT_USER_LIST;
    }

    @PostMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/edit")
    public String updateUser(@Valid @ModelAttribute("user") UserUpdateRequestDTO userUpdateDTO,
                             Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return EDIT_USER_VIEW;
        }
        
        // Check for duplicate username or email (excluding current user)
        var existingUserByUsername = userService.getUserByUsername(userUpdateDTO.getUsername());
        if (existingUserByUsername.isPresent() && 
            !existingUserByUsername.get().getUserId().equals(userUpdateDTO.getUserId())) {
            model.addAttribute("usernameError", "Username already exists");
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return EDIT_USER_VIEW;
        }
        
        var existingUserByEmail = userService.getUserByEmail(userUpdateDTO.getEmail());
        if (existingUserByEmail.isPresent() && 
            !existingUserByEmail.get().getUserId().equals(userUpdateDTO.getUserId())) {
            model.addAttribute("emailError", "Email already exists");
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return EDIT_USER_VIEW;
        }
        
        // Get the existing user and update only the allowed fields
        // Use the new updateUserProfile method that doesn't touch the password
        userService.updateUserProfile(
            userUpdateDTO.getUserId(),
            userUpdateDTO.getFirstName(),
            userUpdateDTO.getLastName(),
            userUpdateDTO.getUsername(),
            userUpdateDTO.getEmail(),
            Boolean.TRUE.equals(userUpdateDTO.getEnabled()),
            userUpdateDTO.getRoleNames()
        );
        
        return REDIRECT_USER_LIST;
    }

    @GetMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/delete/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return REDIRECT_USER_LIST;
    }
}
