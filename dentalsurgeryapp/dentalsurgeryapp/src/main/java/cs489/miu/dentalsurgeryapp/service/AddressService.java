package cs489.miu.dentalsurgeryapp.service;

 
import java.util.List;
import cs489.miu.dentalsurgeryapp.dto.AddressResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.AddressWithPatientsResponseDTO;
import cs489.miu.dentalsurgeryapp.model.Address;

public interface AddressService {
    Address addNewAddress(Address address);
    List<Address> getAllAddresses();
    Address getAddressById(Integer id);
    Address updateAddress(Address address);
    boolean deleteAddressById(Integer id);
    List<AddressResponseDTO> getAllAddressesSortedByCity();
    Address findOrCreateAddress(Address address);
    List<AddressWithPatientsResponseDTO> getAllAddressesWithPatientsSortedByCity();
}
