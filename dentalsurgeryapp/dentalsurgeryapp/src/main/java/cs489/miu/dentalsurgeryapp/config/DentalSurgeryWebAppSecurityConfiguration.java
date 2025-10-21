package cs489.miu.dentalsurgeryapp.config;

import cs489.miu.dentalsurgeryapp.service.impl.DentalSurgeryUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class DentalSurgeryWebAppSecurityConfiguration {
    private final UserDetailsService dentalSurgeryUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public DentalSurgeryWebAppSecurityConfiguration(DentalSurgeryUserDetailsService dentalSurgeryUserDetailsService,
                                                   CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.dentalSurgeryUserDetailsService = dentalSurgeryUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(
                        auth -> {
                            auth
                                    .requestMatchers("/resources/static/**").permitAll()
                                    .requestMatchers("/images/**").permitAll()
                                    .requestMatchers("/css/**").permitAll()
                                    .requestMatchers("/js/**").permitAll()
                                    .requestMatchers("/").permitAll()
                                    .requestMatchers("/dentalsurgery").permitAll()
                                    .requestMatchers("/about").permitAll()
                                    .requestMatchers("/services").permitAll()
                                    .requestMatchers("/contact").permitAll()
                                    .requestMatchers("/public/**").permitAll()
                                    .requestMatchers("/dentalsugery/api/**").permitAll()
                                    .requestMatchers("/secured/appointment/my-appointments").hasRole("DENTIST")
                                    .requestMatchers("/secured/patient/history").hasRole("PATIENT")
                                    .requestMatchers("/secured/**").hasRole("SYSADMIN") 
                                    .requestMatchers("/dentalsurgeryapp/rolebase/patient/**").hasAnyRole("PATIENT")
                                    .requestMatchers("/dentalsurgeryapp/rolebase/dentist/**").hasAnyRole("DENTIST") 
                                    .anyRequest().authenticated();
                        }
                )
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                        .loginPage("/public/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/public/login?error").permitAll())
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutRequestMatcher(new AntPathRequestMatcher("/public/logout"))
                        .logoutSuccessUrl("/public/login?logout").permitAll())
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(dentalSurgeryUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
