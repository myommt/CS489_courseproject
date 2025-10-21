package cs489.miu.dentalsurgeryapp.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.model.SurgeryLocation;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    
    // Find appointment by patient, dentist, datetime, and surgery location (to avoid duplicates)
    Appointment findByPatientAndDentistAndAppointmentDateTimeAndSurgeryLocation(
        Patient patient,
        Dentist dentist,
        LocalDateTime appointmentDateTime,
        SurgeryLocation surgeryLocation
    );
    
    // Count appointments for a dentist within a date range (for weekly limit validation)
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.dentist = :dentist AND a.appointmentDateTime BETWEEN :startDate AND :endDate")
    long countByDentistAndAppointmentDateTimeBetween(
        @Param("dentist") Dentist dentist,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // New methods for patient portal
    Page<Appointment> findByPatient(Patient patient, Pageable pageable);
    
    Page<Appointment> findByPatientAndAppointmentStatus(Patient patient, String status, Pageable pageable);
    
    long countByPatient(Patient patient);
    
    long countByPatientAndAppointmentDateTimeAfter(Patient patient, LocalDateTime dateTime);
    
    long countByPatientAndAppointmentStatus(Patient patient, String status);
    
    // Methods for dentist portal
    Page<Appointment> findByDentist(Dentist dentist, Pageable pageable);
    
    Page<Appointment> findByDentistAndAppointmentStatus(Dentist dentist, String status, Pageable pageable);
    
    long countByDentist(Dentist dentist);
    
    long countByDentistAndAppointmentDateTimeAfter(Dentist dentist, LocalDateTime dateTime);
    
    long countByDentistAndAppointmentStatus(Dentist dentist, String status);
    
}
