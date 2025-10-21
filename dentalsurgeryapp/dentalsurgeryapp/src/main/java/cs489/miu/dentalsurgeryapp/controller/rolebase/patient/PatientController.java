package cs489.miu.dentalsurgeryapp.controller.rolebase.patient;

import cs489.miu.dentalsurgeryapp.dto.AppointmentRequestDTO;
import cs489.miu.dentalsurgeryapp.dto.request.UserUpdateRequestDTO;
import cs489.miu.dentalsurgeryapp.exception.AppointmentLimitExceededException;
import cs489.miu.dentalsurgeryapp.exception.OutstandingBillException;
import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.model.AppointmentStatus;
import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.model.Address;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.model.SurgeryLocation;
import cs489.miu.dentalsurgeryapp.model.User;
import cs489.miu.dentalsurgeryapp.service.AppointmentService;
import cs489.miu.dentalsurgeryapp.service.DentistService;
import cs489.miu.dentalsurgeryapp.service.PatientService;
import cs489.miu.dentalsurgeryapp.service.SurgeryLocationService;
import cs489.miu.dentalsurgeryapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@Controller("patientRolebaseController")
@RequestMapping("/dentalsurgeryapp/rolebase/patient")
@PreAuthorize("hasAuthority('PATIENT')")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DentistService dentistService;

    @Autowired
    private SurgeryLocationService surgeryLocationService;

    /**
     * Patient Dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        // Get recent appointments
        Pageable pageable = PageRequest.of(0, 5, Sort.by("appointmentDateTime").descending());
        Page<Appointment> recentAppointments = appointmentService.findAppointmentsByPatient(currentPatient, pageable);

        // Get appointment counts
        long totalAppointments = appointmentService.countAppointmentsByPatient(currentPatient);
        long upcomingAppointments = appointmentService.countUpcomingAppointmentsByPatient(currentPatient);
        long completedAppointments = appointmentService.countCompletedAppointmentsByPatient(currentPatient);

        model.addAttribute("patient", currentPatient);
        model.addAttribute("recentAppointments", recentAppointments.getContent());
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("completedAppointments", completedAppointments);

        return "rolebase/patient/dashboard";
    }

    /**
     * Patient Profile - View
     */
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        // Get current user from authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        UserUpdateRequestDTO userDto = new UserUpdateRequestDTO();
        userDto.setUserId(currentUser.getUserId());
        userDto.setUsername(currentUser.getUsername());
        userDto.setEmail(currentUser.getEmail());
        userDto.setFirstName(currentUser.getFirstName());
        userDto.setLastName(currentUser.getLastName());
        userDto.setEnabled(currentUser.isEnabled());

        // Ensure address object is non-null so form fields can render values
        if (currentPatient.getAddress() == null) {
            currentPatient.setAddress(new Address());
        }
        model.addAttribute("patient", currentPatient);
        model.addAttribute("user", userDto);

        return "rolebase/patient/profile";
    }

    /**
     * Patient Profile - Edit
     */
    @PostMapping("/profile/edit")
    public String editProfile(@Valid @ModelAttribute("user") UserUpdateRequestDTO userDto,
                              BindingResult bindingResult,
                              @RequestParam("dob") String dob,
                              @RequestParam("contactNumber") String contactNumber,
                              @RequestParam("street") String street,
                              @RequestParam("city") String city,
                              @RequestParam("state") String state,
                              @RequestParam("zipcode") String zipcode,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("patient", currentPatient);
            model.addAttribute("errorMessage", "Please correct the errors in the form");
            return "rolebase/patient/profile";
        }

        try {
            // Update user information
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            userService.updateUser(user);

            // Update patient-specific information
            if (dob != null && !dob.isEmpty()) {
                currentPatient.setDob(LocalDate.parse(dob));
            }
            currentPatient.setContactNumber(contactNumber);
            // Update or create address: only proceed if all fields provided (avoid partial invalid address)
            String st = street == null ? "" : street.trim();
            String ct = city == null ? "" : city.trim();
            String stt = state == null ? "" : state.trim();
            String zp = zipcode == null ? "" : zipcode.trim();

            boolean allProvided = !st.isEmpty() && !ct.isEmpty() && !stt.isEmpty() && !zp.isEmpty();
            if (allProvided) {
                // Important: don't mutate existing Address entity to avoid updating its record.
                // Instead, create a new Address instance with desired values; service will
                // find existing by fields or create a new one and link it.
                Address addr = new Address(null, st, ct, stt, zp);
                currentPatient.setAddress(addr);
            }
            patientService.updatePatient(currentPatient);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/dentalsurgeryapp/rolebase/patient/profile";

        } catch (Exception e) {
            model.addAttribute("patient", currentPatient);
            model.addAttribute("user", userDto);
            model.addAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "rolebase/patient/profile";
        }
    }

    /**
     * Patient Appointments - List
     */
    @GetMapping("/appointments")
    public String listAppointments(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(defaultValue = "appointmentDateTime") String sortBy,
                                   @RequestParam(defaultValue = "desc") String sortDir,
                                   @RequestParam(required = false) String status,
                                   Model model) {
        
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Appointment> appointments;

        if (status != null && !status.isEmpty()) {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            appointments = appointmentService.findAppointmentsByPatientAndStatus(currentPatient, appointmentStatus, pageable);
        } else {
            appointments = appointmentService.findAppointmentsByPatient(currentPatient, pageable);
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentStatus", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "rolebase/patient/appointments";
    }

    /**
     * New Appointment - Form
     */
    @GetMapping("/appointments/new")
    public String newAppointmentForm(Model model) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        AppointmentRequestDTO appointmentDto = new AppointmentRequestDTO();
        appointmentDto.setAppointmentType("ONLINE");
        appointmentDto.setUrgency("MEDIUM"); // Set default urgency
        
        // Pre-populate patient ID from currently logged-in patient
        if (currentPatient.getPatientId() != null) {
            appointmentDto.setPatientId(currentPatient.getPatientId().longValue());
        }
        
        List<Dentist> dentists = dentistService.findAllDentists();
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
        
        model.addAttribute("appointment", appointmentDto);
        model.addAttribute("dentists", dentists);
        model.addAttribute("surgeryLocations", surgeryLocations);

        return "rolebase/patient/appointment-new";
    }

    /**
     * New Appointment - Submit
     */
    @PostMapping("/appointments/new")
    public String createAppointment(@Valid @ModelAttribute("appointment") AppointmentRequestDTO appointmentDto,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }
        
        // Debug: Log submitted data
        System.err.println("=== FORM SUBMISSION DEBUG ===");
        System.err.println("Form PatientId: " + appointmentDto.getPatientId());
        System.err.println("Current Patient ID: " + (currentPatient != null ? currentPatient.getPatientId() : "null"));
        System.err.println("AppointmentDate: " + appointmentDto.getAppointmentDate());
        System.err.println("AppointmentTime: " + appointmentDto.getAppointmentTime());
        System.err.println("DentistId: " + appointmentDto.getDentistId());
        System.err.println("Reason: " + appointmentDto.getReason());
        System.err.println("AppointmentType: " + appointmentDto.getAppointmentType());
        System.err.println("Status: " + appointmentDto.getStatus());
        System.err.println("=== END DEBUG ===");

        if (bindingResult.hasErrors()) {
            List<Dentist> dentists = dentistService.findAllDentists();
            List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
            model.addAttribute("dentists", dentists);
            model.addAttribute("surgeryLocations", surgeryLocations);
            
            // Debug: Log binding errors
            System.err.println("=== BINDING ERRORS ===");
            bindingResult.getAllErrors().forEach(error -> 
                System.err.println("Error: " + error.getDefaultMessage()));
            System.err.println("=== END BINDING ERRORS ===");
            
            model.addAttribute("errorMessage", "Please correct the errors in the form");
            return "rolebase/patient/appointment-new";
        }

        try {
            // Validate required fields
            if (appointmentDto.getAppointmentDate() == null) {
                throw new IllegalArgumentException("Appointment date is required");
            }
            if (appointmentDto.getAppointmentTime() == null || appointmentDto.getAppointmentTime().trim().isEmpty()) {
                throw new IllegalArgumentException("Appointment time is required");
            }
            if (appointmentDto.getDentistId() == null) {
                throw new IllegalArgumentException("Dentist selection is required");
            }
            if (appointmentDto.getReason() == null || appointmentDto.getReason().trim().isEmpty()) {
                throw new IllegalArgumentException("Reason for visit is required");
            }

            // Verify patient ID is bound from form - if not, set it from current patient
            if (appointmentDto.getPatientId() == null) {
                if (currentPatient.getPatientId() != null) {
                    appointmentDto.setPatientId(currentPatient.getPatientId().longValue());
                } else {
                    throw new IllegalArgumentException("Current patient ID is null");
                }
            }
            
            // Verify the form's patient ID matches the logged-in patient (security check)
            if (!appointmentDto.getPatientId().equals(currentPatient.getPatientId().longValue())) {
                throw new IllegalArgumentException("Patient ID mismatch - security violation");
            }
            
            // Set default status if not already set
            if (appointmentDto.getStatus() == null) {
                appointmentDto.setStatus(AppointmentStatus.PENDING);
            }
            
            // Ensure appointment type is set
            if (appointmentDto.getAppointmentType() == null || appointmentDto.getAppointmentType().trim().isEmpty()) {
                appointmentDto.setAppointmentType("ONLINE");
            }

            Appointment appointment = appointmentService.createAppointment(appointmentDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Appointment booked successfully! Reference ID: " + appointment.getAppointmentId());
            return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";

        } catch (AppointmentLimitExceededException | OutstandingBillException e) {
            List<Dentist> dentists = dentistService.findAllDentists();
            List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
            model.addAttribute("dentists", dentists);
            model.addAttribute("surgeryLocations", surgeryLocations);
            model.addAttribute("errorMessage", e.getMessage());
            return "rolebase/patient/appointment-new";
        } catch (IllegalArgumentException e) {
            List<Dentist> dentists = dentistService.findAllDentists();
            List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
            model.addAttribute("dentists", dentists);
            model.addAttribute("surgeryLocations", surgeryLocations);
            model.addAttribute("errorMessage", e.getMessage());
            return "rolebase/patient/appointment-new";
        } catch (Exception e) {
            List<Dentist> dentists = dentistService.findAllDentists();
            List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
            model.addAttribute("dentists", dentists);
            model.addAttribute("surgeryLocations", surgeryLocations);
            model.addAttribute("errorMessage", "Error booking appointment: " + e.getMessage());
            return "rolebase/patient/appointment-new";
        }
    }

    /**
     * Edit Appointment - Form
     */
    @GetMapping("/appointments/{id}/edit")
    public String editAppointmentForm(@PathVariable Long id, Model model) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(id);
        if (appointmentOpt.isEmpty()) {
            return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";
        }

        Appointment appointment = appointmentOpt.get();
        
        // Check if appointment belongs to current patient
        if (!appointment.getPatient().getPatientId().equals(currentPatient.getPatientId())) {
            return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";
        }

        List<Dentist> dentists = dentistService.findAllDentists();
        
        // Convert entity to DTO for form binding
        AppointmentRequestDTO appointmentDto = new AppointmentRequestDTO();
        appointmentDto.setAppointmentId(appointment.getAppointmentId().longValue());
        appointmentDto.setAppointmentType(appointment.getAppointmentType());
        appointmentDto.setAppointmentStatus(appointment.getAppointmentStatus());
        appointmentDto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        appointmentDto.setDentistId(appointment.getDentist().getDentistId().longValue());
        appointmentDto.setSurgeryLocationId(appointment.getSurgeryLocation().getSurgeryLocationId().longValue());
        appointmentDto.setPatientId(appointment.getPatient().getPatientId().longValue());
        
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
        
        model.addAttribute("appointment", appointmentDto);
        model.addAttribute("appointmentEntity", appointment); // Keep original for display purposes
        model.addAttribute("dentists", dentists);
        model.addAttribute("surgeryLocations", surgeryLocations);

        return "rolebase/patient/appointment-edit";
    }

    /**
     * Alternate URL pattern handler: /appointments/edit/{id}
     * Some links may accidentally place 'edit' before the id (e.g. /appointments/edit/3).
     * Provide a GET redirect to the canonical URL and a POST delegate to handle form submissions
     * that target the alternate pattern.
     */
    @GetMapping("/appointments/edit/{id}")
    public String editAppointmentFormAlternate(@PathVariable Long id) {
        // Redirect to canonical mapping
        return "redirect:/dentalsurgeryapp/rolebase/patient/appointments/" + id + "/edit";
    }

    /**
     * Alternate URL pattern handler: /appointments/view/{id}
     * Some links may place 'view' before the id (e.g. /appointments/view/3).
     * Provide a GET redirect to the canonical URL.
     */
    @GetMapping("/appointments/view/{id}")
    public String viewAppointmentAlternate(@PathVariable Long id) {
        // Redirect to canonical mapping
        return "redirect:/dentalsurgeryapp/rolebase/patient/appointments/" + id + "/view";
    }

    @PostMapping("/appointments/edit/{id}")
    public String updateAppointmentAlternate(@PathVariable Long id,
                                             @Valid @ModelAttribute("appointment") AppointmentRequestDTO appointmentDto,
                                             BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes,
                                             Model model) {
        // Delegate to existing update handler to reuse validation and business logic
        return updateAppointment(id, appointmentDto, bindingResult, redirectAttributes, model);
    }

    /**
     * Edit Appointment - Submit
     */
    @PostMapping("/appointments/{id}/edit")
    public String updateAppointment(@PathVariable Long id,
                                    @Valid @ModelAttribute("appointment") AppointmentRequestDTO appointmentDto,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(id);
        if (appointmentOpt.isEmpty()) {
            return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";
        }

        Appointment appointment = appointmentOpt.get();
        
        // Check if appointment belongs to current patient
        if (!appointment.getPatient().getPatientId().equals(currentPatient.getPatientId())) {
            return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";
        }

        if (bindingResult.hasErrors()) {
            List<Dentist> dentists = dentistService.findAllDentists();
            List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
            model.addAttribute("appointment", appointment);
            model.addAttribute("dentists", dentists);
            model.addAttribute("surgeryLocations", surgeryLocations);
            model.addAttribute("errorMessage", "Please correct the errors in the form");
            return "rolebase/patient/appointment-edit";
        }

        try {
            appointmentDto.setAppointmentId(id);
            appointmentDto.setPatientId(currentPatient.getPatientId().longValue());
            
            appointmentService.updateAppointment(appointmentDto);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment updated successfully!");
            return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";

        } catch (Exception e) {
            List<Dentist> dentists = dentistService.findAllDentists();
            List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
            model.addAttribute("appointment", appointment);
            model.addAttribute("dentists", dentists);
            model.addAttribute("surgeryLocations", surgeryLocations);
            model.addAttribute("errorMessage", "Error updating appointment: " + e.getMessage());
            return "rolebase/patient/appointment-edit";
        }
    }

    /**
     * Cancel Appointment
     */
    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        try {
            Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(id);
            if (appointmentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Appointment not found");
                return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";
            }

            Appointment appointment = appointmentOpt.get();
            
            // Check if appointment belongs to current patient
            if (!appointment.getPatient().getPatientId().equals(currentPatient.getPatientId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
                return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";
            }

            appointment.setAppointmentStatus(AppointmentStatus.CANCELLED.name());
            appointmentService.saveAppointment(appointment);
            
            redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error cancelling appointment: " + e.getMessage());
        }

        return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";
    }

    /**
     * Get current authenticated patient
     */
    private Patient getCurrentPatient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);
        
        if (userOpt.isEmpty()) {
            return null;
        }

        // Get patient directly from user
        return userOpt.get().getPatient();
    }
    /**
     * View Appointment Details
     */
    @GetMapping("/appointments/{id}/view")
    public String viewAppointment(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Patient currentPatient = getCurrentPatient();
        if (currentPatient == null) {
            return "redirect:/login";
        }

        Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(id);
        if (appointmentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Appointment not found");
            return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";
        }

        Appointment appointment = appointmentOpt.get();
        // Check if appointment belongs to current patient
        if (!appointment.getPatient().getPatientId().equals(currentPatient.getPatientId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
            return "redirect:/dentalsurgeryapp/rolebase/patient/appointments";
        }

        model.addAttribute("appointment", appointment);
        return "rolebase/patient/appointment-view";
    }
}