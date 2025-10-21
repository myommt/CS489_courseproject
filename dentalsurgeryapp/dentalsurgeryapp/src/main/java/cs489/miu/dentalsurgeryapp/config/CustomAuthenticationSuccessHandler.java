package cs489.miu.dentalsurgeryapp.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Custom authentication success handler that redirects users to role-specific dashboards
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = determineTargetUrl(authorities);
        
        if (response.isCommitted()) {
            return;
        }
        
        response.sendRedirect(redirectUrl);
    }

    /**
     * Determines the target URL based on user roles
     */
    private String determineTargetUrl(Collection<? extends GrantedAuthority> authorities) {
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            
            switch (role) {
                case "ROLE_DENTIST":
                    return "/dentalsurgeryapp/rolebase/dentist/dashboard";
                case "ROLE_PATIENT":
                    return "/dentalsurgeryapp/rolebase/patient/dashboard";
                case "ROLE_SYSADMIN":
                    return "/secured/dashboard";
                default:
                    break;
            }
        }
        
        // Default fallback
        return "/secured/dashboard";
    }
}