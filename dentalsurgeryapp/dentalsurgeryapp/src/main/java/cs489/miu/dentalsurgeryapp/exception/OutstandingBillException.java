package cs489.miu.dentalsurgeryapp.exception;

public class OutstandingBillException extends Exception {
    
    public OutstandingBillException(String message) {
        super(message);
    }
    
    public OutstandingBillException(String message, Throwable cause) {
        super(message, cause);
    }
}
