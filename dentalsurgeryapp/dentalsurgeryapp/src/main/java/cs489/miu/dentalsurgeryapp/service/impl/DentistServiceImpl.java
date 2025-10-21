package cs489.miu.dentalsurgeryapp.service.impl;


import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.repository.DentistRepository;
import cs489.miu.dentalsurgeryapp.service.DentistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DentistServiceImpl implements DentistService {

    private final DentistRepository dentistRepository;

    @Autowired
    public DentistServiceImpl(DentistRepository dentistRepository) {
        this.dentistRepository = dentistRepository;
    }

    @Override
    public Dentist saveDentist(Dentist dentist) {
        if (dentist == null) {
            throw new IllegalArgumentException("Dentist cannot be null");
        }
        
        // If dentist has an ID, it's an update - save directly
        if (dentist.getDentistId() != null) {
            return dentistRepository.save(dentist);
        }
        
        // For new dentists, use findOrCreate to prevent duplicates
        return findOrCreateDentist(dentist);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Dentist> findDentistById(Integer dentistId) {
        if (dentistId == null) {
            return Optional.empty();
        }
        return dentistRepository.findById(dentistId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Dentist> findDentistByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return dentistRepository.findByEmail(email.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dentist> findDentistsByFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return List.of();
        }
        return dentistRepository.findByFirstName(firstName.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dentist> findDentistsByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            return List.of();
        }
        return dentistRepository.findByLastName(lastName.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dentist> findDentistsBySpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            return List.of();
        }
        return dentistRepository.findBySpecialization(specialization.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dentist> findDentistsByFullName(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty() || 
            lastName == null || lastName.trim().isEmpty()) {
            return List.of();
        }
        return dentistRepository.findByFullName(firstName.trim(), lastName.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dentist> getAllDentists() {
        return dentistRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dentist> getAllDentistsOrderedByName() {
        return dentistRepository.findAllOrderedByName();
    }

    @Override
    public Optional<Dentist> updateDentist(Integer dentistId, Dentist dentist) {
        if (dentistId == null || dentist == null) {
            return Optional.empty();
        }
        
        return dentistRepository.findById(dentistId)
                .map(existingDentist -> {
                    existingDentist.setFirstName(dentist.getFirstName());
                    existingDentist.setLastName(dentist.getLastName());
                    existingDentist.setSpecialization(dentist.getSpecialization());
                    existingDentist.setContactNumber(dentist.getContactNumber());
                    existingDentist.setEmail(dentist.getEmail());
                    return dentistRepository.save(existingDentist);
                });
    }

    @Override
    public boolean deleteDentistById(Integer dentistId) {
        if (dentistId != null && dentistRepository.existsById(dentistId)) {
            dentistRepository.deleteById(dentistId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer dentistId) {
        if (dentistId == null) {
            return false;
        }
        return dentistRepository.existsById(dentistId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return dentistRepository.existsByEmail(email.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalDentistCount() {
        return dentistRepository.count();
    }

    @Override
    public Dentist findOrCreateDentist(Dentist dentist) {
        // Check if dentist exists by email (assuming email is unique identifier)
        if (dentist.getEmail() != null && !dentist.getEmail().trim().isEmpty()) {
            Dentist existingDentist = dentistRepository.findByEmailIgnoreCase(dentist.getEmail());
            if (existingDentist != null) {
                return existingDentist;
            }
        }
        
        // Dentist doesn't exist, create new one
        return dentistRepository.save(dentist);
    }
}
