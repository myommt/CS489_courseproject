package cs489.miu.dentalsurgeryapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cs489.miu.dentalsurgeryapp.model.Bill;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.Appointment;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    
    // Find bills by patient
    List<Bill> findByPatient(Patient patient);
    
    // Find bills by payment status
    List<Bill> findByPaymentStatus(String paymentStatus);
    
    // Find bill by appointment (since it's one-to-one)
    Bill findByAppointment(Appointment appointment);
    
    // Find bills by patient ID
    List<Bill> findByPatientPatientId(Integer patientId);
    
    // Find bills by payment status ordered by total cost descending
    @Query("SELECT b FROM Bill b WHERE b.paymentStatus = :paymentStatus ORDER BY b.totalCost DESC")
    List<Bill> findByPaymentStatusOrderByTotalCostDesc(@Param("paymentStatus") String paymentStatus);
    
    // Find all bills ordered by total cost descending
    List<Bill> findAllByOrderByTotalCostDesc();
    
    // Check if patient has any unpaid bills
    @Query("SELECT COUNT(b) FROM Bill b WHERE b.patient = :patient AND b.paymentStatus != 'PAID'")
    long countUnpaidBillsByPatient(@Param("patient") Patient patient);
    
    // Find unpaid bills by patient
    @Query("SELECT b FROM Bill b WHERE b.patient = :patient AND b.paymentStatus != 'PAID'")
    List<Bill> findUnpaidBillsByPatient(@Param("patient") Patient patient);
}
