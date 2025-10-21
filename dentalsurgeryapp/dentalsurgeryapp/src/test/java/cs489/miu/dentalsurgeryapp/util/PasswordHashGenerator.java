package cs489.miu.dentalsurgeryapp.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String plainPassword = "admin123";
        String hashedPassword = encoder.encode(plainPassword);
        System.out.println("Plain password: " + plainPassword);
        System.out.println("Hashed password: " + hashedPassword);
        
        // Test the hash
        boolean matches = encoder.matches(plainPassword, hashedPassword);
        System.out.println("Hash matches: " + matches);
    }
}