package cs489.miu.dentalsurgeryapp.dto;

public record AddressResponseDTO (
    Integer addressId,
    String street,
    String city,
    String state,
    String zipcode){

}
