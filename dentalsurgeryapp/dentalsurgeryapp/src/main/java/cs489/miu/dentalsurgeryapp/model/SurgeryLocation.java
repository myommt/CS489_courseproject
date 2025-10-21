package cs489.miu.dentalsurgeryapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "surgerylocations")
public class SurgeryLocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "surgerylocation_id")
    private Integer surgeryLocationId;
    
    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Surgery location name is required and cannot be blank.")
    private String name;
    
    @Column(name = "contactNumber")
    private String contactNumber;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address location;
}
