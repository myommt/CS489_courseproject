package cs489.miu.dentalsurgeryapp.service;

 
import cs489.miu.dentalsurgeryapp.model.Dentist;

import java.util.List;
import java.util.Optional;

public interface DentistService {
    
    // Save a new dentist or update existing dentist
    Dentist saveDentist(Dentist dentist);
    
    // Find dentist by ID
    Optional<Dentist> findDentistById(Integer dentistId);
    
    // Find dentist by email
    Optional<Dentist> findDentistByEmail(String email);
    
    // Find dentists by first name
    List<Dentist> findDentistsByFirstName(String firstName);
    
    // Find dentists by last name
    List<Dentist> findDentistsByLastName(String lastName);
    
    // Find dentists by specialization
    List<Dentist> findDentistsBySpecialization(String specialization);
    
    // Find dentists by full name
    List<Dentist> findDentistsByFullName(String firstName, String lastName);
    
    // Get all dentists
    List<Dentist> getAllDentists();
    
    // Get all dentists ordered by name
    List<Dentist> getAllDentistsOrderedByName();
    
    // Update dentist
    Optional<Dentist> updateDentist(Integer dentistId, Dentist dentist);
    
    // Delete dentist by ID
    boolean deleteDentistById(Integer dentistId);
    
    // Check if dentist exists by ID
    boolean existsById(Integer dentistId);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Get total count of dentists
    long getTotalDentistCount();
    
    // Find or create dentist (to avoid duplicates)
    Dentist findOrCreateDentist(Dentist dentist);
    
    // Alias for getAllDentists for consistency with PatientController
    default List<Dentist> findAllDentists() {
        return getAllDentists();
    }
}
