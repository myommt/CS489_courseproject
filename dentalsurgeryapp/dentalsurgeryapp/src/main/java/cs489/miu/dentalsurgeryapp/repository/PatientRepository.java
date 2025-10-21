package cs489.miu.dentalsurgeryapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.Address;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    
    // Method to find all patients sorted by last name in ascending order
    List<Patient> findAllByOrderByLastNameAsc();
    
    // Method to search patients by first name, last name, or email (case insensitive)
    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email);
    
    // Method to find all patients by address
    List<Patient> findByAddress(Address address);
    
    // Method to check if patient exists by email (assuming email is unique identifier)
    Patient findByEmail(String email);
    
}
