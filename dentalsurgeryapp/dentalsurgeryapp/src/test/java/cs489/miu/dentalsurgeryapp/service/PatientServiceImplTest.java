package cs489.miu.dentalsurgeryapp.service;

import cs489.miu.dentalsurgeryapp.exception.PatientNotFoundException;
import cs489.miu.dentalsurgeryapp.model.Address;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.repository.PatientRepository;
import cs489.miu.dentalsurgeryapp.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient newPatient;
    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setAddressId(10);
        address.setStreet("123 Main St");
        address.setCity("Fairfield");
        address.setState("IA");
        address.setZipcode("52557");

        newPatient = new Patient();
        newPatient.setFirstName("John");
        newPatient.setLastName("Doe");
        newPatient.setContactNumber("555-1234");
        newPatient.setEmail("john.doe@example.com");
        newPatient.setDob(LocalDate.of(1990, 1, 1));
        newPatient.setAddress(address);
    }

    @Test
    void addNewPatient_createsNew_whenEmailDoesNotExist() {
        // Arrange
        when(patientRepository.findByEmail(eq("john.doe@example.com"))).thenReturn(null);
        when(addressService.findOrCreateAddress(any(Address.class))).thenReturn(address);
        Patient saved = clonePatient(newPatient);
        saved.setPatientId(1);
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        // Act
        Patient result = patientService.addNewPatient(newPatient);

        // Assert
        assertThat(result.getPatientId()).isEqualTo(1);
        assertThat(result.getAddress()).isEqualTo(address);
        verify(addressService).findOrCreateAddress(any(Address.class));
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void addNewPatient_returnsExisting_whenEmailExists() {
        // Arrange
        Patient existing = clonePatient(newPatient);
        existing.setPatientId(2);
        when(patientRepository.findByEmail(eq("john.doe@example.com"))).thenReturn(existing);

        // Act
        Patient result = patientService.addNewPatient(newPatient);

        // Assert
        assertThat(result).isEqualTo(existing);
        verify(patientRepository, never()).save(any());
        verify(addressService, never()).findOrCreateAddress(any());
    }

    @Test
    void getPatientById_returnsPatient_whenFound() throws Exception {
        // Arrange
        Patient existing = clonePatient(newPatient);
        existing.setPatientId(5);
        when(patientRepository.findById(5)).thenReturn(Optional.of(existing));

        // Act
        Patient result = patientService.getPatientById(5);

        // Assert
        assertThat(result).isEqualTo(existing);
    }

    @Test
    void getPatientById_throws_whenNotFound() {
        when(patientRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> patientService.getPatientById(99))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updatePatient_updatesFields_andSaves() throws Exception {
        // Arrange existing patient in DB
        Patient existing = new Patient();
        existing.setPatientId(7);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setEmail("john.doe@example.com");
        existing.setDob(LocalDate.of(1985, 5, 5));

        when(patientRepository.findById(7)).thenReturn(Optional.of(existing));
        when(addressService.findOrCreateAddress(any(Address.class))).thenReturn(address);

        Patient toUpdate = clonePatient(newPatient);
        toUpdate.setPatientId(7);
        toUpdate.setFirstName("John");
        toUpdate.setLastName("Doe");
        toUpdate.setDob(LocalDate.of(1990, 1, 1));
        toUpdate.setAddress(address);

        Patient saved = clonePatient(toUpdate);
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        // Act
        Patient result = patientService.updatePatient(toUpdate);

        // Assert
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getDob()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(result.getAddress()).isEqualTo(address);
        verify(addressService).findOrCreateAddress(any(Address.class));
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void deletePatientById_returnsTrue_whenExists() {
        when(patientRepository.existsById(12)).thenReturn(true);

        boolean result = patientService.deletePatientById(12);

        assertThat(result).isTrue();
        verify(patientRepository).deleteById(12);
    }

    @Test
    void deletePatientById_returnsFalse_whenNotExists() {
        when(patientRepository.existsById(42)).thenReturn(false);

        boolean result = patientService.deletePatientById(42);

        assertThat(result).isFalse();
        verify(patientRepository, never()).deleteById(any());
    }

    private static Patient clonePatient(Patient p) {
        Patient c = new Patient();
        c.setPatientId(p.getPatientId());
        c.setFirstName(p.getFirstName());
        c.setLastName(p.getLastName());
        c.setContactNumber(p.getContactNumber());
        c.setEmail(p.getEmail());
        c.setDob(p.getDob());
        c.setAddress(p.getAddress());
        return c;
    }
}
