package cs489.miu.dentalsurgeryapp.dto.response;

public class LoginResponseDTO {
    
    private String token;
    private String tokenType;
    private UserResponseDTO user;
    private String message;

    public LoginResponseDTO() {
        this.tokenType = "Bearer";
    }

    public LoginResponseDTO(String token, UserResponseDTO user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.user = user;
        this.message = "Login successful";
    }

    public LoginResponseDTO(String token, UserResponseDTO user, String message) {
        this.token = token;
        this.tokenType = "Bearer";
        this.user = user;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}