package cs489.miu.dentalsurgeryapp.service;

import cs489.miu.dentalsurgeryapp.dto.AppointmentRequestDTO;
import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.model.AppointmentStatus;
import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.exception.AppointmentLimitExceededException;
import cs489.miu.dentalsurgeryapp.exception.OutstandingBillException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    
    Appointment addNewAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException;
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Integer id);
    Appointment updateAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException;
    boolean deleteAppointmentById(Integer id);
    Appointment findOrCreateAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException;
    
    // New methods for patient portal
    Optional<Appointment> findAppointmentById(Long id);
    Page<Appointment> findAppointmentsByPatient(Patient patient, Pageable pageable);
    Page<Appointment> findAppointmentsByPatientAndStatus(Patient patient, AppointmentStatus status, Pageable pageable);
    long countAppointmentsByPatient(Patient patient);
    long countUpcomingAppointmentsByPatient(Patient patient);
    long countCompletedAppointmentsByPatient(Patient patient);
    Appointment createAppointment(AppointmentRequestDTO appointmentDto) throws AppointmentLimitExceededException, OutstandingBillException;
    Appointment updateAppointment(AppointmentRequestDTO appointmentDto) throws AppointmentLimitExceededException, OutstandingBillException;
    Appointment saveAppointment(Appointment appointment);
    
    // Methods for dentist portal
    Page<Appointment> findAppointmentsByDentist(Dentist dentist, Pageable pageable);
    Page<Appointment> findAppointmentsByDentistAndStatus(Dentist dentist, AppointmentStatus status, Pageable pageable);
    long countAppointmentsByDentist(Dentist dentist);
    long countUpcomingAppointmentsByDentist(Dentist dentist);
    long countCompletedAppointmentsByDentist(Dentist dentist);
    
}
