package cs489.miu.dentalsurgeryapp.dto;

import java.time.LocalDate;

public record PatientResponseDTO (
    Integer patientId,
    String firstName,
    String lastName,
    String contactNumber,
    String email,
    LocalDate dob,  
    AddressResponseDTO addressResponseDTO
) {
    
}
