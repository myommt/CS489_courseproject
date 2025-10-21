package cs489.miu.dentalsurgeryapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cs489.miu.dentalsurgeryapp.service.PatientService;
import cs489.miu.dentalsurgeryapp.service.DentistService;
import cs489.miu.dentalsurgeryapp.service.AppointmentService;
import cs489.miu.dentalsurgeryapp.service.BillService;

/**
 * Controller for handling system administration pages
 */
@Controller
@RequestMapping("/secured")
public class SysAdminController {

    private final PatientService patientService;
    private final DentistService dentistService;
    private final AppointmentService appointmentService;
    private final BillService billService;

    public SysAdminController(PatientService patientService, 
                             DentistService dentistService,
                             AppointmentService appointmentService,
                             BillService billService) {
        this.patientService = patientService;
        this.dentistService = dentistService;
        this.appointmentService = appointmentService;
        this.billService = billService;
    }

    /**
     * Main admin dashboard
     */
    @GetMapping({"/", "/index", "/dashboard"})
    public String adminDashboard(Model model) {
        // Add statistics for the dashboard
        try {
            int totalPatients = patientService.getAllPatients().size();
            int totalDentists = dentistService.getAllDentists().size();
            
            // Get today's appointments count
            int todayAppointments = (int) appointmentService.getAllAppointments().stream()
                .filter(appointment -> {
                    if (appointment.getAppointmentDateTime() != null) {
                        return appointment.getAppointmentDateTime().toLocalDate().equals(java.time.LocalDate.now());
                    }
                    return false;
                })
                .count();
            
            // Get pending bills count (bills with paymentStatus not "Paid")
            int pendingBills = (int) billService.getAllBills().stream()
                .filter(bill -> bill.paymentStatus() != null && !"Paid".equalsIgnoreCase(bill.paymentStatus()))
                .count();
            
            model.addAttribute("totalPatients", totalPatients);
            model.addAttribute("totalDentists", totalDentists);
            model.addAttribute("todayAppointments", todayAppointments);
            model.addAttribute("pendingBills", pendingBills);
        } catch (Exception e) {
            // Handle any service exceptions gracefully
            model.addAttribute("totalPatients", 0);
            model.addAttribute("totalDentists", 0);
            model.addAttribute("todayAppointments", 0);
            model.addAttribute("pendingBills", 0);
        }
        
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "secured/index";
    }

    /**
     * System settings page
     */
    @GetMapping("/settings")
    public String systemSettings(Model model) {
        model.addAttribute("pageTitle", "System Settings");
        return "secured/settings";
    }

    /**
     * User management page
     */
    @GetMapping("/users")
    public String userManagement(Model model) {
        model.addAttribute("pageTitle", "User Management");
        return "secured/users";
    }

    /**
     * Reports page
     */
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "Reports");
        return "secured/reports";
    }

    /**
     * System logs page
     */
    @GetMapping("/logs")
    public String systemLogs(Model model) {
        model.addAttribute("pageTitle", "System Logs");
        return "secured/logs";
    }

    /**
     * Backup and restore page
     */
    @GetMapping("/backup")
    public String backupRestore(Model model) {
        model.addAttribute("pageTitle", "Backup & Restore");
        return "secured/backup";
    }

    /**
     * System maintenance page
     */
    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        model.addAttribute("pageTitle", "System Maintenance");
        return "secured/maintenance";
    }

    /**
     * Analytics and statistics page
     */
    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("pageTitle", "Analytics");
        return "secured/analytics";
    }

    /**
     * Configuration management page
     */
    @GetMapping("/config")
    public String configuration(Model model) {
        model.addAttribute("pageTitle", "Configuration");
        return "secured/config";
    }
}