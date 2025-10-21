package cs489.miu.dentalsurgeryapp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record BillRequestDTO(
    @NotNull(message = "Total cost is required")
    @DecimalMin(value = "0.00", message = "Total cost must be non-negative")
    BigDecimal totalCost,
    
    @NotBlank(message = "Payment status is required")
    String paymentStatus,
    
    @NotNull(message = "Patient ID is required")
    Integer patientId,
    
    @NotNull(message = "Appointment ID is required")
    Integer appointmentId
) {
}
