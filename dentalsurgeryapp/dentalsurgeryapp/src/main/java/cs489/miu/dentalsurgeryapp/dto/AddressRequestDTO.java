package cs489.miu.dentalsurgeryapp.dto;

public record AddressRequestDTO(
    String street,
    String city,
    String state,
    String zipcode
) {

}
