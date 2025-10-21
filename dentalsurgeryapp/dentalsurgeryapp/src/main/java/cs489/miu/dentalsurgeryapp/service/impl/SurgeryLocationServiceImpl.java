package cs489.miu.dentalsurgeryapp.service.impl;

 
import cs489.miu.dentalsurgeryapp.model.SurgeryLocation;
import cs489.miu.dentalsurgeryapp.model.Address;
import cs489.miu.dentalsurgeryapp.repository.SurgeryLocationRepository;
import cs489.miu.dentalsurgeryapp.service.AddressService;
import cs489.miu.dentalsurgeryapp.service.SurgeryLocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SurgeryLocationServiceImpl implements SurgeryLocationService {

    private final SurgeryLocationRepository surgeryLocationRepository;
    private final AddressService addressService;

    @Autowired
    public SurgeryLocationServiceImpl(SurgeryLocationRepository surgeryLocationRepository, AddressService addressService) {
        this.surgeryLocationRepository = surgeryLocationRepository;
        this.addressService = addressService;
    }

    @Override
    public SurgeryLocation saveSurgeryLocation(SurgeryLocation surgeryLocation) {
        if (surgeryLocation == null) {
            throw new IllegalArgumentException("Surgery location cannot be null");
        }
        // Use findOrCreate to prevent duplicates and handle address relationships
        return findOrCreateSurgeryLocation(surgeryLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SurgeryLocation> findSurgeryLocationById(Integer surgeryLocationId) {
        if (surgeryLocationId == null) {
            return Optional.empty();
        }
        return surgeryLocationRepository.findById(surgeryLocationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SurgeryLocation> findSurgeryLocationByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return surgeryLocationRepository.findByName(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> findSurgeryLocationsByContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            return List.of();
        }
        return surgeryLocationRepository.findByContactNumber(contactNumber.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> findSurgeryLocationsByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return List.of();
        }
        return surgeryLocationRepository.findByCity(city.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> findSurgeryLocationsByState(String state) {
        if (state == null || state.trim().isEmpty()) {
            return List.of();
        }
        return surgeryLocationRepository.findByState(state.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> findSurgeryLocationsByZipcode(String zipcode) {
        if (zipcode == null || zipcode.trim().isEmpty()) {
            return List.of();
        }
        return surgeryLocationRepository.findByZipcode(zipcode.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> getAllSurgeryLocations() {
        return surgeryLocationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> getAllSurgeryLocationsOrderedByName() {
        return surgeryLocationRepository.findAllOrderedByName();
    }

    @Override
    public Optional<SurgeryLocation> updateSurgeryLocation(Integer surgeryLocationId, SurgeryLocation surgeryLocation) {
        if (surgeryLocationId == null || surgeryLocation == null) {
            return Optional.empty();
        }
        
        return surgeryLocationRepository.findById(surgeryLocationId)
                .map(existingSurgeryLocation -> {
                    existingSurgeryLocation.setName(surgeryLocation.getName());
                    existingSurgeryLocation.setContactNumber(surgeryLocation.getContactNumber());
                    // Link address via find-or-create to avoid mutating existing address rows
                    if (surgeryLocation.getLocation() != null) {
                        Address managedAddress = addressService.findOrCreateAddress(surgeryLocation.getLocation());
                        existingSurgeryLocation.setLocation(managedAddress);
                    }
                    return surgeryLocationRepository.save(existingSurgeryLocation);
                });
    }

    @Override
    public boolean deleteSurgeryLocationById(Integer surgeryLocationId) {
        if (surgeryLocationId != null && surgeryLocationRepository.existsById(surgeryLocationId)) {
            surgeryLocationRepository.deleteById(surgeryLocationId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer surgeryLocationId) {
        if (surgeryLocationId == null) {
            return false;
        }
        return surgeryLocationRepository.existsById(surgeryLocationId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return surgeryLocationRepository.existsByName(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalSurgeryLocationCount() {
        return surgeryLocationRepository.count();
    }

    @Override
    public SurgeryLocation findOrCreateSurgeryLocation(SurgeryLocation surgeryLocation) {
        // Check if surgery location exists by name and location
        if (surgeryLocation.getName() != null && !surgeryLocation.getName().trim().isEmpty() 
            && surgeryLocation.getLocation() != null) {
            
            // Use findOrCreate for address first
            Address managedAddress = 
                addressService.findOrCreateAddress(surgeryLocation.getLocation());
            
            SurgeryLocation existingLocation = surgeryLocationRepository.findByNameAndLocation(
                surgeryLocation.getName(), managedAddress);
            if (existingLocation != null) {
                return existingLocation;
            }
            
            // Set the managed address before saving
            surgeryLocation.setLocation(managedAddress);
        }
        
        // Surgery location doesn't exist, create new one
        return surgeryLocationRepository.save(surgeryLocation);
    }
}
