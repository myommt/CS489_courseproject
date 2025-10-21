package cs489.miu.dentalsurgeryapp.dto;

import java.math.BigDecimal;

public record BillResponseDTO(
    Integer billId,
    BigDecimal totalCost,
    String paymentStatus,
    PatientBasicInfoDTO patient,
    AppointmentBasicInfoDTO appointment
) {
    public record PatientBasicInfoDTO(
        Integer patientId,
        String firstName,
        String lastName,
        String email
    ) {}
    
    public record AppointmentBasicInfoDTO(
        Integer appointmentId,
        String appointmentType,
        String appointmentStatus,
        java.time.LocalDateTime appointmentDateTime
    ) {}
}
