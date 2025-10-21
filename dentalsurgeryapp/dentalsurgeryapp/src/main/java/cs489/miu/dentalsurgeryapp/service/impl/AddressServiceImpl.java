package cs489.miu.dentalsurgeryapp.service.impl;
 

import java.util.List;
import org.springframework.stereotype.Service;
import cs489.miu.dentalsurgeryapp.dto.AddressResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.AddressWithPatientsResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.AddressWithPatientsResponseDTO.PatientBasicInfoDTO;
import cs489.miu.dentalsurgeryapp.model.Address;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.repository.AddressRepository;
import cs489.miu.dentalsurgeryapp.repository.PatientRepository;
import cs489.miu.dentalsurgeryapp.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final PatientRepository patientRepository;

    public AddressServiceImpl(AddressRepository addressRepository, PatientRepository patientRepository) {
        this.addressRepository = addressRepository;
        this.patientRepository = patientRepository;
    }    @Override
    public Address addNewAddress(Address address) {
        return findOrCreateAddress(address);
    }

    @Override
    public Address findOrCreateAddress(Address address) {
        // Check if address with same street, city, state, and zipcode already exists
        return addressRepository.findByStreetAndCityAndStateAndZipcode(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipcode()
        ).orElseGet(() -> addressRepository.save(address));
    }

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public Address getAddressById(Integer id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public Address updateAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public boolean deleteAddressById(Integer id) {
        if (addressRepository.existsById(id)) {
            addressRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<AddressResponseDTO> getAllAddressesSortedByCity() {
        return addressRepository.findAllByOrderByCityAsc()
                .stream()
                .map(address -> new AddressResponseDTO(
                        address.getAddressId(),
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getZipcode()
                ))
                .toList();
    }

    @Override
    public List<AddressWithPatientsResponseDTO> getAllAddressesWithPatientsSortedByCity() {
        List<Address> addresses = addressRepository.findAllByOrderByCityAsc();
        
        return addresses.stream()
                .map(address -> {
                    List<Patient> patients = patientRepository.findByAddress(address);
                    List<PatientBasicInfoDTO> patientDTOs = patients.stream()
                            .map(patient -> new PatientBasicInfoDTO(
                                    patient.getPatientId(),
                                    patient.getFirstName(),
                                    patient.getLastName(),
                                    patient.getContactNumber(),
                                    patient.getEmail(),
                                    patient.getDob()
                            ))
                            .toList();
                    
                    return new AddressWithPatientsResponseDTO(
                            address.getAddressId(),
                            address.getStreet(),
                            address.getCity(),
                            address.getState(),
                            address.getZipcode(),
                            patientDTOs
                    );
                })
                .toList();
    }
}

