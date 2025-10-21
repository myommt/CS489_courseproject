package cs489.miu.dentalsurgeryapp.dto;

public record DentistResponseDTO(
    Integer dentistId,
    String firstName,
    String lastName,
    String contactNumber,
    String email,
    String specialization
) {
}
