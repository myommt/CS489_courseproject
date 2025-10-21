package cs489.miu.dentalsurgeryapp.controller.rolebase.dentist;

import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.model.AppointmentStatus;
import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.model.User;
import cs489.miu.dentalsurgeryapp.service.AppointmentService;
import cs489.miu.dentalsurgeryapp.service.DentistService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller("dentistRolebaseController")
@RequestMapping("/dentalsurgeryapp/rolebase/dentist")
@PreAuthorize("hasAuthority('DENTIST')")
public class DentistController {

    // Constants
    private static final String DENTIST_DASHBOARD_VIEW = "rolebase/dentist/dashboard";
    private static final String DENTIST_PROFILE_VIEW = "rolebase/dentist/profile";
    private static final String DENTIST_APPOINTMENTS_VIEW = "rolebase/dentist/appointments";
    private static final String DENTIST_APPOINTMENT_EDIT_VIEW = "rolebase/dentist/appointment-edit";
    private static final String REDIRECT_DENTIST_DASHBOARD = "redirect:/dentalsurgeryapp/rolebase/dentist/dashboard";
    private static final String REDIRECT_DENTIST_APPOINTMENTS = "redirect:/dentalsurgeryapp/rolebase/dentist/appointments";

    private final DentistService dentistService;
    private final AppointmentService appointmentService;

    public DentistController(DentistService dentistService, AppointmentService appointmentService) {
        this.dentistService = dentistService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Dentist dentist = getDentistFromAuthentication(authentication);
        if (dentist == null) {
            model.addAttribute("error", "Dentist profile not found. Please contact administrator.");
            return "error/403";
        }

        // Get dashboard statistics
        long totalAppointments = appointmentService.countAppointmentsByDentist(dentist);
        long upcomingAppointments = appointmentService.countUpcomingAppointmentsByDentist(dentist);
        long completedAppointments = appointmentService.countCompletedAppointmentsByDentist(dentist);

        model.addAttribute("dentist", dentist);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("completedAppointments", completedAppointments);

        return DENTIST_DASHBOARD_VIEW;
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        Dentist dentist = getDentistFromAuthentication(authentication);
        if (dentist == null) {
            model.addAttribute("error", "Dentist profile not found. Please contact administrator.");
            return "error/403";
        }

        model.addAttribute("dentist", dentist);
        return DENTIST_PROFILE_VIEW;
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("dentist") Dentist dentist,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return DENTIST_PROFILE_VIEW;
        }

        // Verify this dentist belongs to the authenticated user
        Dentist currentDentist = getDentistFromAuthentication(authentication);
        if (currentDentist == null || !currentDentist.getDentistId().equals(dentist.getDentistId())) {
            model.addAttribute("error", "Unauthorized access");
            return "error/403";
        }

        try {
            dentistService.saveDentist(dentist);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return REDIRECT_DENTIST_DASHBOARD;
        } catch (Exception e) {
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
            return DENTIST_PROFILE_VIEW;
        }
    }

    @GetMapping("/appointments")
    public String viewAppointments(Authentication authentication,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "appointmentDateTime") String sortBy,
                                 @RequestParam(defaultValue = "asc") String sortDir,
                                 @RequestParam(required = false) String status,
                                 Model model) {
        Dentist dentist = getDentistFromAuthentication(authentication);
        if (dentist == null) {
            model.addAttribute("error", "Dentist profile not found. Please contact administrator.");
            return "error/403";
        }

        // Create pageable with sorting
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Appointment> appointments;
        if (status != null && !status.trim().isEmpty()) {
            try {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
                appointments = appointmentService.findAppointmentsByDentistAndStatus(dentist, appointmentStatus, pageable);
            } catch (IllegalArgumentException e) {
                appointments = appointmentService.findAppointmentsByDentist(dentist, pageable);
            }
        } else {
            appointments = appointmentService.findAppointmentsByDentist(dentist, pageable);
        }

        model.addAttribute("dentist", dentist);
        model.addAttribute("appointments", appointments);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("appointmentStatuses", AppointmentStatus.values());

        return DENTIST_APPOINTMENTS_VIEW;
    }

    @GetMapping("/appointments/{appointmentId}/edit")
    public String editAppointment(@PathVariable Long appointmentId,
                                Authentication authentication,
                                Model model) {
        Dentist dentist = getDentistFromAuthentication(authentication);
        if (dentist == null) {
            model.addAttribute("error", "Dentist profile not found. Please contact administrator.");
            return "error/403";
        }

        Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            model.addAttribute("error", "Appointment not found.");
            return REDIRECT_DENTIST_APPOINTMENTS;
        }

        Appointment appointment = appointmentOpt.get();
        
        // Verify this appointment belongs to the authenticated dentist
        if (!appointment.getDentist().getDentistId().equals(dentist.getDentistId())) {
            model.addAttribute("error", "Unauthorized access to this appointment.");
            return "error/403";
        }

        model.addAttribute("appointment", appointment);
        model.addAttribute("appointmentStatuses", AppointmentStatus.values());
        
        return DENTIST_APPOINTMENT_EDIT_VIEW;
    }

    @PostMapping("/appointments/{appointmentId}/edit")
    public String updateAppointment(@PathVariable Long appointmentId,
                                  @Valid @ModelAttribute("appointment") Appointment appointment,
                                  BindingResult bindingResult,
                                  Authentication authentication,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("appointmentStatuses", AppointmentStatus.values());
            return DENTIST_APPOINTMENT_EDIT_VIEW;
        }

        Dentist dentist = getDentistFromAuthentication(authentication);
        if (dentist == null) {
            model.addAttribute("error", "Dentist profile not found. Please contact administrator.");
            return "error/403";
        }

        // Verify this appointment belongs to the authenticated dentist
        if (!appointment.getDentist().getDentistId().equals(dentist.getDentistId())) {
            model.addAttribute("error", "Unauthorized access to this appointment.");
            return "error/403";
        }

        try {
            // Use validated update to enforce weekly limit and other business rules
            appointmentService.updateAppointment(appointment);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment updated successfully!");
            return REDIRECT_DENTIST_APPOINTMENTS;
        } catch (Exception e) {
            model.addAttribute("error", "Error updating appointment: " + e.getMessage());
            model.addAttribute("appointmentStatuses", AppointmentStatus.values());
            return DENTIST_APPOINTMENT_EDIT_VIEW;
        }
    }

    /**
     * Helper method to get the Dentist associated with the authenticated User
     */
    private Dentist getDentistFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            Optional<Dentist> dentistOpt = dentistService.findDentistByEmail(user.getEmail());
            return dentistOpt.orElse(null);
        }
        return null;
    }
}