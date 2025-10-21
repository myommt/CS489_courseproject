package cs489.miu.dentalsurgeryapp.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Scanner;

/**
 * Enhanced Password Utility for BCrypt encryption and verification
 * 
 * Features:
 * 1. Generate BCrypt hash for any password
 * 2. Verify if a plain password matches a BCrypt hash
 * 3. Interactive mode for easy testing
 * 4. Batch mode for quick operations
 * 
 * Usage:
 * - Run without arguments for interactive mode
 * - Run with 1 argument to generate hash: java PasswordGenerator "mypassword"
 * - Run with 2 arguments to verify: java PasswordGenerator "mypassword" "hash"
 */
public class PasswordGenerator {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public static void main(String[] args) {
        if (args.length == 0) {
            // Interactive mode
            runInteractiveMode();
        } else if (args.length == 1) {
            // Generate hash mode
            generateHash(args[0]);
        } else if (args.length == 2) {
            // Verify password mode
            verifyPassword(args[0], args[1]);
        } else {
            printUsage();
        }
    }
    
    /**
     * Interactive mode - allows user to choose encrypt or decrypt operations
     */
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===========================================");
        System.out.println("  🔐 Password Encryption/Verification Tool");
        System.out.println("===========================================");
        
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. 🔒 Encrypt password (Generate BCrypt hash)");
            System.out.println("2. 🔓 Verify password (Check against hash)");
            System.out.println("3. 📋 Generate multiple passwords");
            System.out.println("4. ❌ Exit");
            System.out.print("Enter your choice (1-4): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    encryptPasswordInteractive(scanner);
                    break;
                case "2":
                    verifyPasswordInteractive(scanner);
                    break;
                case "3":
                    generateMultiplePasswords(scanner);
                    break;
                case "4":
                    System.out.println("👋 Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please enter 1, 2, 3, or 4.");
            }
        }
    }
    
    /**
     * Encrypt password interactively
     */
    private static void encryptPasswordInteractive(Scanner scanner) {
        System.out.print("Enter password to encrypt: ");
        String password = scanner.nextLine();
        generateHash(password);
    }
    
    /**
     * Verify password interactively
     */
    private static void verifyPasswordInteractive(Scanner scanner) {
        System.out.print("Enter plain password: ");
        String plainPassword = scanner.nextLine();
        System.out.print("Enter BCrypt hash: ");
        String hash = scanner.nextLine();
        verifyPassword(plainPassword, hash);
    }
    
    /**
     * Generate multiple passwords for common test cases
     */
    private static void generateMultiplePasswords(Scanner scanner) {
        System.out.println("\n🔄 Generating common test passwords...");
        
        String[] commonPasswords = {"admin123", "ts123", "jb123", "test123", "password123"};
        
        System.out.println("\n📋 Generated BCrypt Hashes:");
        System.out.println("=".repeat(70));
        System.out.printf("%-15s | %-50s%n", "Password", "BCrypt Hash");
        System.out.println("=".repeat(70));
        
        for (String password : commonPasswords) {
            String hash = passwordEncoder.encode(password);
            System.out.printf("%-15s | %-50s%n", password, hash);
        }
        System.out.println("=".repeat(70));
        
        // Ask if user wants to add custom passwords
        System.out.print("\nWould you like to add custom passwords? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("y") || response.equals("yes")) {
            while (true) {
                System.out.print("Enter password (or 'done' to finish): ");
                String customPassword = scanner.nextLine().trim();
                if (customPassword.equalsIgnoreCase("done")) {
                    break;
                }
                if (!customPassword.isEmpty()) {
                    generateHash(customPassword);
                }
            }
        }
    }
    
    /**
     * Generate BCrypt hash for a given password
     */
    public static void generateHash(String rawPassword) {
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        System.out.println("\n✅ Password Encryption Result:");
        System.out.println("─".repeat(50));
        System.out.println("Plain password: " + rawPassword);
        System.out.println("BCrypt hash:    " + hashedPassword);
        System.out.println("Hash length:    " + hashedPassword.length());
        System.out.println("─".repeat(50));
        
        // For easy copying to data.sql
        System.out.println("\n📄 Ready for data.sql:");
        System.out.println("'" + hashedPassword + "'");
    }
    
    /**
     * Verify if a plain password matches a BCrypt hash
     */
    public static void verifyPassword(String plainPassword, String hash) {
        boolean matches = passwordEncoder.matches(plainPassword, hash);
        
        System.out.println("\n🔍 Password Verification Result:");
        System.out.println("─".repeat(50));
        System.out.println("Plain password: " + plainPassword);
        System.out.println("BCrypt hash:    " + hash);
        System.out.println("Match result:   " + (matches ? "✅ VALID" : "❌ INVALID"));
        System.out.println("─".repeat(50));
        
        if (!matches) {
            System.out.println("💡 Tip: Make sure the hash is complete and not truncated");
        }
    }
    
    /**
     * Print usage instructions
     */
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java PasswordGenerator                    - Interactive mode");
        System.out.println("  java PasswordGenerator \"password\"         - Generate hash");
        System.out.println("  java PasswordGenerator \"password\" \"hash\" - Verify password");
    }
    
    /**
     * Utility method for other classes to use
     */
    public static String encryptPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    /**
     * Utility method for other classes to use - returns boolean only
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}