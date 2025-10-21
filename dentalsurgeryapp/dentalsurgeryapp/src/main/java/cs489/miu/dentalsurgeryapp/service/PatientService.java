package cs489.miu.dentalsurgeryapp.service;

 
import cs489.miu.dentalsurgeryapp.exception.PatientNotFoundException;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.User;
import cs489.miu.dentalsurgeryapp.dto.PatientResponseDTO;
import java.util.List;
import java.util.Optional;

public interface PatientService {

    Patient addNewPatient(Patient patient);
    List<PatientResponseDTO> getAllPatients();  
    List<PatientResponseDTO> getAllPatientsSortedByLastName();
    Patient getPatientById(Integer id) throws PatientNotFoundException; 
    Patient updatePatient(Patient patient) throws PatientNotFoundException;
    boolean deletePatientById(Integer id);
    List<Patient> searchPatients(String searchString);
    Patient findOrCreatePatient(Patient patient);

}

