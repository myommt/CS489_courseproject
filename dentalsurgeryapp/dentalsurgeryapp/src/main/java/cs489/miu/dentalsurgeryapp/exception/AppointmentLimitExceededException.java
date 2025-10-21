package cs489.miu.dentalsurgeryapp.exception;

public class AppointmentLimitExceededException extends Exception {
    
    public AppointmentLimitExceededException(String message) {
        super(message);
    }
    
    public AppointmentLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
