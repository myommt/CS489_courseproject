package cs489.miu.dentalsurgeryapp.service;

import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.repository.DentistRepository;
import cs489.miu.dentalsurgeryapp.service.impl.DentistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DentistServiceImplTest {

    @Mock
    private DentistRepository dentistRepository;

    @InjectMocks
    private DentistServiceImpl dentistService;

    @Captor
    private ArgumentCaptor<Dentist> dentistCaptor;

    private Dentist sampleDentist;
    private AutoCloseable mocksCloseable;

    @BeforeEach
    void setUp() {
        // Initialize Mockito annotations manually instead of using the MockitoExtension
        mocksCloseable = MockitoAnnotations.openMocks(this);
        sampleDentist = new Dentist();
        sampleDentist.setFirstName("John");
        sampleDentist.setLastName("Doe");
        sampleDentist.setEmail("john.doe@example.com");
        sampleDentist.setSpecialization("Orthodontics");
        sampleDentist.setContactNumber("555-0100");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocksCloseable != null) {
            mocksCloseable.close();
        }
    }

    @Test
    void saveDentist_null_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dentistService.saveDentist(null));
        verifyNoInteractions(dentistRepository);
    }

    @Test
    void saveDentist_newDentist_callsFindOrCreateAndSaves() {
        // repository returns null for findByEmailIgnoreCase -> will save
        when(dentistRepository.findByEmailIgnoreCase(anyString())).thenReturn(null);
        when(dentistRepository.save(any(Dentist.class))).thenAnswer(invocation -> {
            Dentist d = invocation.getArgument(0);
            d.setDentistId(42);
            return d;
        });

        Dentist saved = dentistService.saveDentist(sampleDentist);

        verify(dentistRepository).findByEmailIgnoreCase("john.doe@example.com");
        verify(dentistRepository).save(dentistCaptor.capture());

        Dentist captured = dentistCaptor.getValue();
        assertThat(captured.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(saved.getDentistId()).isEqualTo(42);
    }

    @Test
    void saveDentist_withId_callsRepositorySaveDirectly() {
        Dentist existing = new Dentist();
        existing.setDentistId(100);
        existing.setFirstName("Jane");
        existing.setLastName("Smith");
        when(dentistRepository.save(existing)).thenReturn(existing);

        Dentist result = dentistService.saveDentist(existing);

        verify(dentistRepository, times(1)).save(existing);
        assertThat(result.getDentistId()).isEqualTo(100);
    }

    @Test
    void findDentistById_null_returnsEmpty() {
        Optional<Dentist> opt = dentistService.findDentistById(null);
        assertThat(opt).isEmpty();
        verifyNoInteractions(dentistRepository);
    }

    @Test
    void deleteDentistById_existing_deletesAndReturnsTrue() {
        when(dentistRepository.existsById(5)).thenReturn(true);
        doNothing().when(dentistRepository).deleteById(5);

        boolean deleted = dentistService.deleteDentistById(5);

        assertThat(deleted).isTrue();
        verify(dentistRepository).deleteById(5);
    }

    @Test
    void deleteDentistById_missing_returnsFalse() {
        when(dentistRepository.existsById(999)).thenReturn(false);

        boolean deleted = dentistService.deleteDentistById(999);

        assertThat(deleted).isFalse();
        verify(dentistRepository, never()).deleteById(any());
    }
}
