package cs489.miu.dentalsurgeryapp.dto;

import java.time.LocalDate;

public record PatientRequestDTO(
    String firstName,
    String lastName,
    String contactNumber,
    String email,
    LocalDate dob,  
    AddressRequestDTO addressRequestDTO
) {

}
