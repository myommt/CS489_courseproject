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
@Table(name = "addresses")
public class Address {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer addressId;
    
    @Column(name = "street", nullable = false, length = 100)
    @NotBlank(message="street is required and cannot be blank or empty.")
    private String street;

    @Column(name = "city", nullable = false, length = 100)
    @NotBlank(message="city is required and cannot be blank or empty.")
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    @NotBlank(message="state is required and cannot be blank or empty.")
    private String state;
    
    @Column(name = "zipcode", nullable = false, length = 100)
    @NotBlank(message="zipcode is required and cannot be blank or empty.")
    private String zipcode;

}

