package cs489.miu.dentalsurgeryapp.repository;


import cs489.miu.dentalsurgeryapp.model.SurgeryLocation;
import cs489.miu.dentalsurgeryapp.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurgeryLocationRepository extends JpaRepository<SurgeryLocation, Integer> {
    
    // Find surgery location by name
    Optional<SurgeryLocation> findByName(String name);
    
    // Find surgery locations by contact number
    List<SurgeryLocation> findByContactNumber(String contactNumber);
    
    // Find surgery locations by city (through location relationship)
    @Query("SELECT sl FROM SurgeryLocation sl JOIN sl.location a WHERE a.city = :city")
    List<SurgeryLocation> findByCity(@Param("city") String city);
    
    // Find surgery locations by state (through location relationship)
    @Query("SELECT sl FROM SurgeryLocation sl JOIN sl.location a WHERE a.state = :state")
    List<SurgeryLocation> findByState(@Param("state") String state);
    
    // Find surgery locations by zipcode (through location relationship)
    @Query("SELECT sl FROM SurgeryLocation sl JOIN sl.location a WHERE a.zipcode = :zipcode")
    List<SurgeryLocation> findByZipcode(@Param("zipcode") String zipcode);
    
    // Find all surgery locations ordered by name
    @Query("SELECT sl FROM SurgeryLocation sl ORDER BY sl.name")
    List<SurgeryLocation> findAllOrderedByName();
    
    // Check if name exists
    boolean existsByName(String name);
    
    // Find surgery location by name and address (for findOrCreate - to avoid duplicates)
    @Query("SELECT sl FROM SurgeryLocation sl WHERE sl.name = :name AND sl.location = :location")
    SurgeryLocation findByNameAndLocation(@Param("name") String name, @Param("location") Address location);
}
