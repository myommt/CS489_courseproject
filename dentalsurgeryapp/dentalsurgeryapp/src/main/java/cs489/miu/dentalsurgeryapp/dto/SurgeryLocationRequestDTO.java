package cs489.miu.dentalsurgeryapp.dto;

public record SurgeryLocationRequestDTO(
    String name,
    String contactNumber,
    AddressRequestDTO addressRequestDTO
) {

}
