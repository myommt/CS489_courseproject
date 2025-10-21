package cs489.miu.dentalsurgeryapp.dto;

import java.time.LocalDate;
import java.util.List;

public record AddressWithPatientsResponseDTO(
    Integer addressId,
    String street,
    String city,
    String state,
    String zipcode,
    List<PatientBasicInfoDTO> patients
) {
    public record PatientBasicInfoDTO(
        Integer patientId,
        String firstName,
        String lastName,
        String contactNumber,
        String email,
        LocalDate dob
    ) {}
}
