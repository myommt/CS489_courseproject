package cs489.miu.dentalsurgeryapp.dto;

public record SurgeryLocationResponseDTO(
    Integer surgeryLocationId,
    String name,
    String contactNumber,
    AddressResponseDTO location
) {
}
