package cs489.miu.dentalsurgeryapp.service;
 

import cs489.miu.dentalsurgeryapp.model.SurgeryLocation;

import java.util.List;
import java.util.Optional;

public interface SurgeryLocationService {
    
    // Save a new surgery location or update existing surgery location
    SurgeryLocation saveSurgeryLocation(SurgeryLocation surgeryLocation);
    
    // Find surgery location by ID
    Optional<SurgeryLocation> findSurgeryLocationById(Integer surgeryLocationId);
    
    // Find surgery location by name
    Optional<SurgeryLocation> findSurgeryLocationByName(String name);
    
    // Find surgery locations by contact number
    List<SurgeryLocation> findSurgeryLocationsByContactNumber(String contactNumber);
    
    // Find surgery locations by city
    List<SurgeryLocation> findSurgeryLocationsByCity(String city);
    
    // Find surgery locations by state
    List<SurgeryLocation> findSurgeryLocationsByState(String state);
    
    // Find surgery locations by zipcode
    List<SurgeryLocation> findSurgeryLocationsByZipcode(String zipcode);
    
    // Get all surgery locations
    List<SurgeryLocation> getAllSurgeryLocations();
    
    // Get all surgery locations ordered by name
    List<SurgeryLocation> getAllSurgeryLocationsOrderedByName();
    
    // Update surgery location
    Optional<SurgeryLocation> updateSurgeryLocation(Integer surgeryLocationId, SurgeryLocation surgeryLocation);
    
    // Delete surgery location by ID
    boolean deleteSurgeryLocationById(Integer surgeryLocationId);
    
    // Check if surgery location exists by ID
    boolean existsById(Integer surgeryLocationId);
    
    // Check if name exists
    boolean existsByName(String name);
    
    // Get total count of surgery locations
    long getTotalSurgeryLocationCount();
    
    // Find or create surgery location (to avoid duplicates)
    SurgeryLocation findOrCreateSurgeryLocation(SurgeryLocation surgeryLocation);
}
