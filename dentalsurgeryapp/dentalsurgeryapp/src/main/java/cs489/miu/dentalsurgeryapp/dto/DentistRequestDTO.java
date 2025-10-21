package cs489.miu.dentalsurgeryapp.dto;

public record DentistRequestDTO(
    String firstName,
    String lastName,
    String contactNumber,
    String email,
    String specialization
) {

}
