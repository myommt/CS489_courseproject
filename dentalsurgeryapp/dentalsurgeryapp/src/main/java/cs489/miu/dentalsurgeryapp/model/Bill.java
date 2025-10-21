package cs489.miu.dentalsurgeryapp.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "bills")
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Integer billId;
    
    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total cost is required.")
    @DecimalMin(value = "0.00", message = "Total cost must be non-negative.")
    private BigDecimal totalCost;
    
    @Column(name = "payment_status", nullable = false, length = 20)
    @NotNull(message = "Payment status is required.")
    private String paymentStatus; // e.g., "PENDING", "PAID", "OVERDUE", "CANCELLED"
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required.")
    private Patient patient;
    
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    @NotNull(message = "Appointment is required.")
    private Appointment appointment;
}
