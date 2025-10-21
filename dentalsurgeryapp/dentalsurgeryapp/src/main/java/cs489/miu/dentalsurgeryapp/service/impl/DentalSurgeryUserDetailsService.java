package cs489.miu.dentalsurgeryapp.service.impl;

import cs489.miu.dentalsurgeryapp.model.User;
import cs489.miu.dentalsurgeryapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DentalSurgeryUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public DentalSurgeryUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
        return user;
    }
}