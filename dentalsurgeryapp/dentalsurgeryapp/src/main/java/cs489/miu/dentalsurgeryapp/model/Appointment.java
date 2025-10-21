package cs489.miu.dentalsurgeryapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @Column(name = "appointmentType", nullable = false, length = 50)
    @NotBlank(message = "Appointment type is required and cannot be blank.")
    private String appointmentType;

    @Column(name = "appointmentStatus", nullable = false, length = 20)
    @NotBlank(message = "Appointment status is required and cannot be blank.")
    private String appointmentStatus;

    @Column(name = "appointmentDateTime", nullable = false)
    @NotNull(message = "Appointment date and time is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentDateTime;

    // Many appointments can be for one patient
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false) 
    private Patient patient;

    // For now, we'll use a simple string for dentist name
    // You can create a Dentist entity later if needed
    @ManyToOne
    @JoinColumn(name = "dentist_id", nullable = false) 
    private Dentist dentist;

    // For now, we'll use a simple string for surgery location
    // You can create a SurgeryLocation entity later if needed
    @ManyToOne
    @JoinColumn(name = "surgerylocation", nullable = false)
    private SurgeryLocation surgeryLocation;

}
