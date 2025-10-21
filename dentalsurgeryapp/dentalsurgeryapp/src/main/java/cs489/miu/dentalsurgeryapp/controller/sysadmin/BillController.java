package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cs489.miu.dentalsurgeryapp.dto.BillRequestDTO;
import cs489.miu.dentalsurgeryapp.dto.BillResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.DeleteResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.OutstandingBillCheckDTO;
import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.model.Bill;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.service.BillService;
import cs489.miu.dentalsurgeryapp.service.PatientService;
import cs489.miu.dentalsurgeryapp.service.AppointmentService;

/**
 * Unified Bill Controller
 * - Serves MVC pages under /secured/bill
 * - Exposes REST API under /dentalsugery/api/bills
 */
@Controller("billController")
public class BillController {

    private final BillService billService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public BillController(BillService billService, 
                         PatientService patientService,
                         AppointmentService appointmentService) {
        this.billService = billService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    // ===================== MVC (Thymeleaf) endpoints =====================

    @GetMapping({"/secured/bill/", "/secured/bill/list"})
    public String listBills(@RequestParam(required = false) String searchTerm, Model model) {
        List<Bill> bills = billService.getAllBills().stream()
            .map(dto -> {
                Bill bill = new Bill();
                bill.setBillId(dto.billId());
                bill.setTotalCost(dto.totalCost());
                bill.setPaymentStatus(dto.paymentStatus());
                
                Patient patient = new Patient();
                patient.setPatientId(dto.patient().patientId());
                patient.setFirstName(dto.patient().firstName());
                patient.setLastName(dto.patient().lastName());
                patient.setEmail(dto.patient().email());
                bill.setPatient(patient);
                
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(dto.appointment().appointmentId());
                appointment.setAppointmentType(dto.appointment().appointmentType());
                appointment.setAppointmentStatus(dto.appointment().appointmentStatus());
                appointment.setAppointmentDateTime(dto.appointment().appointmentDateTime());
                bill.setAppointment(appointment);
                
                return bill;
            })
            .collect(Collectors.toList());
        
        // Filter by searchTerm if provided
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String searchLower = searchTerm.trim().toLowerCase();
            bills = bills.stream()
                .filter(b -> 
                    (b.getPatient().getFirstName() != null && b.getPatient().getFirstName().toLowerCase().contains(searchLower)) ||
                    (b.getPatient().getLastName() != null && b.getPatient().getLastName().toLowerCase().contains(searchLower)) ||
                    (b.getPaymentStatus() != null && b.getPaymentStatus().toLowerCase().contains(searchLower))
                )
                .collect(Collectors.toList());
        }
        
        model.addAttribute("bills", bills);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("pageTitle", "Bill List");
        return "secured/bill/list";
    }

    @GetMapping("/secured/bill/new")
    public String showNewBillForm(Model model) {
        model.addAttribute("bill", new Bill());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("pageTitle", "Create New Bill");
        return "secured/bill/new";
    }

    @PostMapping("/secured/bill/new")
    public String createBill(@ModelAttribute("bill") Bill bill,
                            RedirectAttributes redirectAttributes) {
        try {
            billService.addNewBill(bill);
            redirectAttributes.addFlashAttribute("successMessage", "Bill created successfully.");
            return "redirect:/secured/bill/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating bill: " + e.getMessage());
            return "redirect:/secured/bill/new";
        }
    }

    @GetMapping("/secured/bill/edit/{id}")
    public String showEditBillForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Bill bill = billService.getBillById(id);
        if (bill == null) {
            ra.addFlashAttribute("errorMessage", "Bill not found with ID: " + id);
            return "redirect:/secured/bill/list";
        }
        model.addAttribute("bill", bill);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("pageTitle", "Edit Bill");
        return "secured/bill/edit";
    }

    @PostMapping("/secured/bill/edit/{id}")
    public String updateBill(@PathVariable Integer id,
                            @ModelAttribute("bill") Bill bill,
                            RedirectAttributes redirectAttributes) {
        try {
            bill.setBillId(id);
            billService.updateBill(bill);
            redirectAttributes.addFlashAttribute("successMessage", "Bill updated successfully.");
            return "redirect:/secured/bill/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating bill: " + e.getMessage());
            return "redirect:/secured/bill/edit/" + id;
        }
    }

    @PostMapping("/secured/bill/delete/{id}")
    public String deleteBill(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean deleted = billService.deleteBillById(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Bill deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete bill.");
        }
        return "redirect:/secured/bill/list";
    }

    // ===================== REST API endpoints =====================

    @GetMapping("/dentalsugery/api/bills")
    @ResponseBody
    public ResponseEntity<List<BillResponseDTO>> getAllBills() {
        List<BillResponseDTO> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/dentalsugery/api/bills/{id}")
    @ResponseBody
    public ResponseEntity<BillResponseDTO> getBillById(@PathVariable Integer id) {
        Bill bill = billService.getBillById(id);
        if (bill != null) {
            return ResponseEntity.ok(mapToDTO(bill));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/dentalsugery/api/bills/patient/{patientId}")
    @ResponseBody
    public ResponseEntity<List<BillResponseDTO>> getBillsByPatientId(@PathVariable Integer patientId) {
        List<BillResponseDTO> bills = billService.getBillsByPatientId(patientId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/dentalsugery/api/bills/status/{paymentStatus}")
    @ResponseBody
    public ResponseEntity<List<BillResponseDTO>> getBillsByPaymentStatus(@PathVariable String paymentStatus) {
        List<BillResponseDTO> bills = billService.getBillsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/dentalsugery/api/bills/patient/{patientId}/outstanding")
    @ResponseBody
    public ResponseEntity<List<BillResponseDTO>> getOutstandingBillsByPatientId(@PathVariable Integer patientId) {
        List<BillResponseDTO> outstandingBills = billService.getOutstandingBillsByPatientId(patientId);
        return ResponseEntity.ok(outstandingBills);
    }

    @GetMapping("/dentalsugery/api/bills/patient/{patientId}/has-outstanding")
    @ResponseBody
    public ResponseEntity<OutstandingBillCheckDTO> hasOutstandingBills(@PathVariable Integer patientId) {
        boolean hasOutstanding = billService.hasOutstandingBills(patientId);
        return ResponseEntity.ok(new OutstandingBillCheckDTO(patientId, hasOutstanding));
    }

    @PostMapping("/dentalsugery/api/bills")
    @ResponseBody
    public ResponseEntity<BillResponseDTO> addNewBill(@RequestBody BillRequestDTO billRequestDTO) {
        try {
            Bill bill = mapToEntity(billRequestDTO);
            Bill createdBill = billService.addNewBill(bill);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdBill));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/dentalsugery/api/bills/{id}")
    @ResponseBody
    public ResponseEntity<BillResponseDTO> updateBillApi(@PathVariable Integer id, @RequestBody BillRequestDTO billRequestDTO) {
        try {
            Bill bill = mapToEntity(billRequestDTO);
            bill.setBillId(id);
            Bill updatedBill = billService.updateBill(bill);
            return ResponseEntity.ok(mapToDTO(updatedBill));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/dentalsugery/api/bills/{id}")
    @ResponseBody
    public ResponseEntity<DeleteResponseDTO> deleteBillApi(@PathVariable Integer id) {
        boolean deleted = billService.deleteBillById(id);
        if (deleted) {
            return ResponseEntity.ok(new DeleteResponseDTO(true, "Bill deleted successfully"));
        } else {
            return ResponseEntity.ok(new DeleteResponseDTO(false, "Bill not found or could not be deleted"));
        }
    }

    private Bill mapToEntity(BillRequestDTO billRequestDTO) {
        Bill bill = new Bill();
        bill.setTotalCost(billRequestDTO.totalCost());
        bill.setPaymentStatus(billRequestDTO.paymentStatus());
        
        // Set patient
        if (billRequestDTO.patientId() != null) {
            try {
                bill.setPatient(patientService.getPatientById(billRequestDTO.patientId()));
            } catch (Exception e) {
                throw new RuntimeException("Patient not found with ID: " + billRequestDTO.patientId());
            }
        }
        
        // Set appointment
        if (billRequestDTO.appointmentId() != null) {
            bill.setAppointment(appointmentService.getAppointmentById(billRequestDTO.appointmentId()));
        }
        
        return bill;
    }

    private BillResponseDTO mapToDTO(Bill bill) {
        BillResponseDTO.PatientBasicInfoDTO patientDTO = new BillResponseDTO.PatientBasicInfoDTO(
                bill.getPatient().getPatientId(),
                bill.getPatient().getFirstName(),
                bill.getPatient().getLastName(),
                bill.getPatient().getEmail()
        );
        
        BillResponseDTO.AppointmentBasicInfoDTO appointmentDTO = new BillResponseDTO.AppointmentBasicInfoDTO(
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
}
