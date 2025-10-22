package cs489.miu.dentalsurgeryapp.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DentistModelTest {

    @Test
    void getFullName_returnsConcatenatedNames_whenBothPresent() {
        Dentist d = new Dentist();
        d.setFirstName("Alice");
        d.setLastName("Wonder");

        String full = d.getFullName();

        assertThat(full).isEqualTo("Alice Wonder");
    }

    @Test
    void getFullName_handlesNullParts() {
        Dentist d1 = new Dentist();
        d1.setFirstName(null);
        d1.setLastName("Solo");

        assertThat(d1.getFullName()).isEqualTo(" Solo");

        Dentist d2 = new Dentist();
        d2.setFirstName("Neo");
        d2.setLastName(null);

        assertThat(d2.getFullName()).isEqualTo("Neo ");
    }
}
