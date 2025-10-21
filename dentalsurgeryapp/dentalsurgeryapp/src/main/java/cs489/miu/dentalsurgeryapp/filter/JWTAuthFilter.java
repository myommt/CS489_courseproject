package cs489.miu.dentalsurgeryapp.filter;

import cs489.miu.dentalsurgeryapp.service.impl.DentalSurgeryUserDetailsService;
import cs489.miu.dentalsurgeryapp.service.util.JWTMgmtUtilityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTMgmtUtilityService jwtMgmtUtilityService;
    private final DentalSurgeryUserDetailsService dentalSurgeryUserDetailsService;

    public JWTAuthFilter(JWTMgmtUtilityService jwtMgmtUtilityService, 
                        DentalSurgeryUserDetailsService dentalSurgeryUserDetailsService) {
        this.jwtMgmtUtilityService = jwtMgmtUtilityService;
        this.dentalSurgeryUserDetailsService = dentalSurgeryUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;
        
        // Extract JWT token from Authorization header
        // Expected format: "Bearer <token>"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                username = jwtMgmtUtilityService.extractUsername(jwtToken);
            } catch (Exception e) {
                // Log the exception if needed
                // Invalid token format or expired token
            }
        }
        
        // If we have a username and no authentication is set in the context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = dentalSurgeryUserDetailsService.loadUserByUsername(username);
                
                // Validate the token
                if (jwtMgmtUtilityService.validateToken(jwtToken, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    
                    // Set additional details
                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                // Log the exception if needed
                // User not found or other authentication errors
            }
        }
        
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
