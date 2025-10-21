package cs489.miu.dentalsurgeryapp.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import cs489.miu.dentalsurgeryapp.model.Bill;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.dto.BillResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.BillResponseDTO.PatientBasicInfoDTO;
import cs489.miu.dentalsurgeryapp.dto.BillResponseDTO.AppointmentBasicInfoDTO;
import cs489.miu.dentalsurgeryapp.repository.BillRepository;
import cs489.miu.dentalsurgeryapp.service.BillService;
import cs489.miu.dentalsurgeryapp.service.PatientService;
import cs489.miu.dentalsurgeryapp.repository.AppointmentRepository;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final PatientService patientService;
    private final AppointmentRepository appointmentRepository;

    public BillServiceImpl(BillRepository billRepository, 
                          PatientService patientService,
                          AppointmentRepository appointmentRepository) {
        this.billRepository = billRepository;
        this.patientService = patientService;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Bill addNewBill(Bill bill) {
        return findOrCreateBill(bill);
    }

    @Override
    public List<BillResponseDTO> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<BillResponseDTO> getAllBillsSortedByTotalCost() {
        return billRepository.findAllByOrderByTotalCostDesc().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public Bill getBillById(Integer id) {
        return billRepository.findById(id).orElse(null);
    }

    @Override
    public Bill updateBill(Bill bill) {
        // Preserve immutable relations (patient, appointment) and only update mutable fields
        Bill existing = billRepository.findById(bill.getBillId()).orElse(null);
        if (existing == null) {
            // If not found, fall back to save (will likely fail if relations are missing)
            return billRepository.save(bill);
        }

        // Update allowed fields
        existing.setTotalCost(bill.getTotalCost());
        existing.setPaymentStatus(bill.getPaymentStatus());

        // Ensure relations remain attached and valid
        // If incoming has nulls (due to disabled fields not posting), keep existing
        if (bill.getPatient() != null && bill.getPatient().getPatientId() != null
                && !bill.getPatient().getPatientId().equals(existing.getPatient().getPatientId())) {
            // Do not allow changing patient on update; keep existing
        }
        if (bill.getAppointment() != null && bill.getAppointment().getAppointmentId() != null
                && !bill.getAppointment().getAppointmentId().equals(existing.getAppointment().getAppointmentId())) {
            // Do not allow changing appointment on update; keep existing
        }

        return billRepository.save(existing);
    }

    @Override
    public boolean deleteBillById(Integer id) {
        if (billRepository.existsById(id)) {
            billRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<BillResponseDTO> getBillsByPatientId(Integer patientId) {
        return billRepository.findByPatientPatientId(patientId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<BillResponseDTO> getBillsByPaymentStatus(String paymentStatus) {
        return billRepository.findByPaymentStatusOrderByTotalCostDesc(paymentStatus).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public Bill findOrCreateBill(Bill bill) {
        // Use findOrCreate for related entities first
        if (bill.getPatient() != null) {
            Patient managedPatient = patientService.findOrCreatePatient(bill.getPatient());
            bill.setPatient(managedPatient);
        }
        
        if (bill.getAppointment() != null) {
            // For bills, we assume the appointment already exists
            // We just save/use the existing appointment ID
            Appointment appointment = bill.getAppointment();
            if (appointment.getAppointmentId() != null) {
                // Use existing appointment
                Appointment existingAppointment = appointmentRepository.findById(appointment.getAppointmentId()).orElse(null);
                if (existingAppointment != null) {
                    bill.setAppointment(existingAppointment);
                }
            }
        }
        
        // Check if bill already exists for this appointment (since it's one-to-one)
        if (bill.getAppointment() != null) {
            Bill existingBill = billRepository.findByAppointment(bill.getAppointment());
            if (existingBill != null) {
                return existingBill;
            }
        }
        
        // Bill doesn't exist, create new one
        return billRepository.save(bill);
    }

    private BillResponseDTO mapToDTO(Bill bill) {
        PatientBasicInfoDTO patientDTO = new PatientBasicInfoDTO(
                bill.getPatient().getPatientId(),
                bill.getPatient().getFirstName(),
                bill.getPatient().getLastName(),
                bill.getPatient().getEmail()
        );
        
        AppointmentBasicInfoDTO appointmentDTO = new AppointmentBasicInfoDTO(
                bill.getAppointment().getAppointmentId(),
                bill.getAppointment().getAppointmentType(),
                bill.getAppointment().getAppointmentStatus(),
                bill.getAppointment().getAppointmentDateTime()
        );
        
        return new BillResponseDTO(
                bill.getBillId(),
                bill.getTotalCost(),
                bill.getPaymentStatus(),
                patientDTO,
                appointmentDTO
        );
    }

    @Override
    public boolean hasOutstandingBills(Integer patientId) {
        try {
            Patient patient = patientService.getPatientById(patientId);
            if (patient == null) {
                return false;
            }
            return billRepository.countUnpaidBillsByPatient(patient) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<BillResponseDTO> getOutstandingBillsByPatientId(Integer patientId) {
        try {
            Patient patient = patientService.getPatientById(patientId);
            if (patient == null) {
                return List.of();
            }
            return billRepository.findUnpaidBillsByPatient(patient).stream()
                    .map(this::mapToDTO)
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }
}
