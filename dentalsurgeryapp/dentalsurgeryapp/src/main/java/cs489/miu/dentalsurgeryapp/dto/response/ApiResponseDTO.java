package cs489.miu.dentalsurgeryapp.dto.response;

public class ApiResponseDTO<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String timestamp;

    public ApiResponseDTO() {
        this.timestamp = java.time.Instant.now().toString();
    }

    public ApiResponseDTO(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    public ApiResponseDTO(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }

    public static <T> ApiResponseDTO<T> success(String message) {
        return new ApiResponseDTO<>(true, message);
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        return new ApiResponseDTO<>(false, message);
    }

    public static <T> ApiResponseDTO<T> error(String message, T data) {
        return new ApiResponseDTO<>(false, message, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}