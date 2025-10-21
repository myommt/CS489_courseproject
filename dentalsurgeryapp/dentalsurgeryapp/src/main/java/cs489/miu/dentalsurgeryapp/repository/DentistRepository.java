package cs489.miu.dentalsurgeryapp.repository;

import cs489.miu.dentalsurgeryapp.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Integer> {
    
    // Find dentist by email
    Optional<Dentist> findByEmail(String email);
    
    // Find dentists by first name
    List<Dentist> findByFirstName(String firstName);
    
    // Find dentists by last name
    List<Dentist> findByLastName(String lastName);
    
    // Find dentists by specialization
    List<Dentist> findBySpecialization(String specialization);
    
    // Find dentists by full name
    @Query("SELECT d FROM Dentist d WHERE d.firstName = :firstName AND d.lastName = :lastName")
    List<Dentist> findByFullName(@Param("firstName") String firstName, @Param("lastName") String lastName);
    
    // Find all dentists ordered by last name, then first name
    @Query("SELECT d FROM Dentist d ORDER BY d.lastName, d.firstName")
    List<Dentist> findAllOrderedByName();
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find dentist by email (returns null if not found, for findOrCreate)
    Dentist findByEmailIgnoreCase(String email);
}
