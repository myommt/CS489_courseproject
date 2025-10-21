package cs489.miu.dentalsurgeryapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Database Initializer Utility
 * 
 * This utility helps with manual database initialization when you need to:
 * 1. Load data.sql manually (when spring.sql.init.mode=never)
 * 2. Clean and reload database data
 * 3. Reset user data for testing
 * 
 * Usage:
 * - Uncomment @Component annotation to enable automatic running
 * - Or call methods manually from other services/controllers
 */
// @Component  // Uncomment this line to enable automatic database initialization on startup
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔧 DatabaseInitializer is active but doing nothing by default");
        System.out.println("💡 To initialize database, call initializeDatabase() method");
        
        // Uncomment the line below to automatically run database initialization
        // initializeDatabase();
    }
    
    /**
     * Initialize database by running data.sql
     */
    public void initializeDatabase() {
        try {
            System.out.println("🚀 Starting database initialization...");
            
            // Read and execute data.sql
            ClassPathResource resource = new ClassPathResource("data.sql");
            String sqlContent = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
            
            // Split by semicolon and execute each statement
            String[] statements = sqlContent.split(";");
            
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    try {
                        jdbcTemplate.execute(trimmed);
                        System.out.println("✅ Executed: " + trimmed.substring(0, Math.min(50, trimmed.length())) + "...");
                    } catch (Exception e) {
                        System.err.println("⚠️ Warning executing: " + trimmed.substring(0, Math.min(50, trimmed.length())) + "...");
                        System.err.println("   Error: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("✅ Database initialization completed!");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clean all user-related data
     */
    public void cleanUserData() {
        try {
            System.out.println("🧹 Cleaning user data...");
            
            jdbcTemplate.execute("DELETE FROM users_roles");
            jdbcTemplate.execute("DELETE FROM users");
            jdbcTemplate.execute("DELETE FROM roles");
            
            System.out.println("✅ User data cleaned!");
            
        } catch (Exception e) {
            System.err.println("❌ Error cleaning user data: " + e.getMessage());
        }
    }
    
    /**
     * Clean and reinitialize database
     */
    public void resetDatabase() {
        System.out.println("🔄 Resetting database...");
        cleanUserData();
        initializeDatabase();
        System.out.println("✅ Database reset completed!");
    }
    
    /**
     * Check if users exist in database
     */
    public void checkDatabaseState() {
        try {
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            Integer roleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM roles", Integer.class);
            
            System.out.println("📊 Database State:");
            System.out.println("   Users: " + userCount);
            System.out.println("   Roles: " + roleCount);
            
            if (userCount != null && userCount > 0) {
                System.out.println("📋 Existing users:");
                jdbcTemplate.query("SELECT username, email FROM users ORDER BY user_id", 
                    (rs, rowNum) -> {
                        System.out.println("   - " + rs.getString("username") + " (" + rs.getString("email") + ")");
                        return null;
                    });
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error checking database state: " + e.getMessage());
        }
    }
}