package cs489.miu.dentalsurgeryapp.service.impl;

 
import java.util.List;
import java.util.Optional;
 
import org.springframework.stereotype.Service;

import cs489.miu.dentalsurgeryapp.exception.PatientNotFoundException;
import cs489.miu.dentalsurgeryapp.model.Address;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.User;
import cs489.miu.dentalsurgeryapp.dto.PatientResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.AddressResponseDTO;
import cs489.miu.dentalsurgeryapp.repository.PatientRepository;
import cs489.miu.dentalsurgeryapp.service.AddressService;
import cs489.miu.dentalsurgeryapp.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AddressService addressService;
    
    public PatientServiceImpl(PatientRepository patientRepository, AddressService addressService) {
        this.patientRepository = patientRepository;
        this.addressService = addressService;
    }

    @Override
    public Patient addNewPatient(Patient patient) {
        // Use findOrCreate to prevent duplicates and handle address relationships
        return findOrCreatePatient(patient);
    }

    @Override
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(patient -> new PatientResponseDTO(
                        patient.getPatientId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getContactNumber(),
                        patient.getEmail(),
                        patient.getDob(),
                            (patient.getAddress() != null)?
                            new AddressResponseDTO(
                                    patient.getAddress().getAddressId(),
                                    patient.getAddress().getStreet(),
                                    patient.getAddress().getCity(),
                                    patient.getAddress().getState(),
                                    patient.getAddress().getZipcode()
                            ):null
                ))
                .toList();
    }

    @Override
    public Patient getPatientById(Integer id) throws PatientNotFoundException {
        return patientRepository.findById(id)
                .orElseThrow(()-> new PatientNotFoundException("Patient with ID " + id + " not found."));
    }

    @Override
    public Patient updatePatient(Patient patient) throws PatientNotFoundException {
        // Get the existing patient to preserve the address relationship
        Patient existingPatient = patientRepository.findById(patient.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + patient.getPatientId() + " not found."));
        
        // Update basic patient fields
        existingPatient.setFirstName(patient.getFirstName());
        existingPatient.setLastName(patient.getLastName());
        existingPatient.setContactNumber(patient.getContactNumber());
        existingPatient.setEmail(patient.getEmail());
        existingPatient.setDob(patient.getDob());
        
        // Handle address update
        if (patient.getAddress() != null) {
            // If patient has new address data, find existing or create new address and link it
            Address savedAddress = addressService.findOrCreateAddress(patient.getAddress());
            existingPatient.setAddress(savedAddress);
        } else {
            // If no address provided in update, keep existing address (don't remove it)
            // existingPatient.setAddress(null); // Uncomment this line if you want to remove address when not provided
        }
        
        return patientRepository.save(existingPatient);
    }

    @Override
    public boolean deletePatientById(Integer id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<PatientResponseDTO> getAllPatientsSortedByLastName() {
        return patientRepository.findAllByOrderByLastNameAsc()
                .stream()
                .map(patient -> new PatientResponseDTO(
                        patient.getPatientId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getContactNumber(),
                        patient.getEmail(),
                        patient.getDob(),
                        (patient.getAddress() != null)?
                        new AddressResponseDTO(
                                patient.getAddress().getAddressId(),
                                patient.getAddress().getStreet(),
                                patient.getAddress().getCity(),
                                patient.getAddress().getState(),
                                patient.getAddress().getZipcode()
                        ):null
                ))
                .toList();
    }

    @Override
    public List<Patient> searchPatients(String searchString) {
        return patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchString, searchString, searchString);
    }

    @Override
    public Patient findOrCreatePatient(Patient patient) {
        // Check if patient exists by email (assuming email is unique identifier)
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            Patient existingPatient = patientRepository.findByEmail(patient.getEmail());
            if (existingPatient != null) {
                return existingPatient;
            }
        }
        
        // If patient has an address, use findOrCreate for address
        if (patient.getAddress() != null) {
            Address managedAddress = addressService.findOrCreateAddress(patient.getAddress());
            patient.setAddress(managedAddress);
        }
        
        // Patient doesn't exist, create new one
        return patientRepository.save(patient);
    }

}

