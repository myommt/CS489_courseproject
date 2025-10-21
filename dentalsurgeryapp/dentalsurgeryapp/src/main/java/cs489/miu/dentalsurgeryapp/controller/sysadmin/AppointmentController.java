package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;
import java.util.Collection;
import java.time.LocalDateTime;
import java.time.DayOfWeek;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cs489.miu.dentalsurgeryapp.dto.*;
import cs489.miu.dentalsurgeryapp.model.*;
import cs489.miu.dentalsurgeryapp.service.*;
import cs489.miu.dentalsurgeryapp.exception.AppointmentLimitExceededException;
import cs489.miu.dentalsurgeryapp.exception.OutstandingBillException;

@Controller("appointmentController")
public class AppointmentController {

    // Constants for repeated strings
    private static final String PAGE_TITLE = "pageTitle";
    private static final String APPOINTMENT = "appointment";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String REDIRECT_APPOINTMENT_LIST = "redirect:/secured/appointment/list";
    private static final String APPOINTMENT_NEW_VIEW = "secured/appointment/new";
    private static final String APPOINTMENT_EDIT_VIEW = "secured/appointment/edit";
    private static final String ADD_NEW_APPOINTMENT_TITLE = "Add New Appointment";
    private static final String EDIT_APPOINTMENT_TITLE = "Edit Appointment";
    private static final String APPOINTMENT_NOT_FOUND_MSG = "Appointment not found with ID: ";

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DentistService dentistService;
    private final SurgeryLocationService surgeryLocationService;
    private final AddressService addressService;
    private final BillService billService;

    public AppointmentController(AppointmentService appointmentService, 
                               PatientService patientService,
                               DentistService dentistService,
                               SurgeryLocationService surgeryLocationService,
                               AddressService addressService,
                               BillService billService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.dentistService = dentistService;
        this.surgeryLocationService = surgeryLocationService;
        this.addressService = addressService;
        this.billService = billService;
    }

    // ===================== MVC (Thymeleaf) endpoints =====================
    @GetMapping({"/secured/appointment/", "/secured/appointment/list"})
    public String listAppointments(Model model, @RequestParam(required = false) String searchTerm) {
    List<Appointment> appointments = appointmentService.getAllAppointments();
    // Safety: drop any transient appointments lacking an ID to avoid invalid links
    appointments = appointments.stream()
        .filter(appt -> appt != null && appt.getAppointmentId() != null)
        .collect(java.util.stream.Collectors.toList());
        
        // Filter by search term if provided
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String search = searchTerm.trim().toLowerCase();
            appointments = appointments.stream()
                .filter(appt -> {
                    boolean matchesPatient = appt.getPatient() != null && 
                        (appt.getPatient().getFirstName().toLowerCase().contains(search) ||
                         appt.getPatient().getLastName().toLowerCase().contains(search));
                    boolean matchesDentist = appt.getDentist() != null && 
                        (appt.getDentist().getFirstName().toLowerCase().contains(search) ||
                         appt.getDentist().getLastName().toLowerCase().contains(search));
                    boolean matchesType = appt.getAppointmentType() != null && 
                        appt.getAppointmentType().toLowerCase().contains(search);
                    boolean matchesStatus = appt.getAppointmentStatus() != null && 
                        appt.getAppointmentStatus().toLowerCase().contains(search);
                    return matchesPatient || matchesDentist || matchesType || matchesStatus;
                })
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Sort by appointmentDateTime descending (most recent first)
        appointments.sort((a1, a2) -> {
            if (a1.getAppointmentDateTime() == null && a2.getAppointmentDateTime() == null) return 0;
            if (a1.getAppointmentDateTime() == null) return 1;
            if (a2.getAppointmentDateTime() == null) return -1;
            return a2.getAppointmentDateTime().compareTo(a1.getAppointmentDateTime());
        });
        
        model.addAttribute("appointments", appointments);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute(PAGE_TITLE, "Appointment List");
        return "secured/appointment/list";
    }

    @GetMapping("/secured/appointment/my-appointments")
    public String listMyAppointments(Model model, Authentication authentication,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "appointmentDateTime") String sortBy,
                                    @RequestParam(defaultValue = "asc") String sortDir,
                                    @RequestParam(required = false) String status) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User currentUser = (User) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        
        // Check if user is a dentist
        boolean isDentist = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_DENTIST"));
        
        if (isDentist && currentUser.getDentist() != null) {
            // Redirect to dentist's role-based appointments page with proper data
            Dentist dentist = currentUser.getDentist();
            
            // Create pageable with sorting
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : 
                       Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Get appointments for this dentist
            Page<Appointment> appointments;
            if (status != null && !status.isEmpty()) {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
                appointments = appointmentService.findAppointmentsByDentistAndStatus(dentist, appointmentStatus, pageable);
            } else {
                appointments = appointmentService.findAppointmentsByDentist(dentist, pageable);
            }
            
            // Add model attributes expected by dentist appointments template
            model.addAttribute("dentist", dentist);
            model.addAttribute("appointments", appointments);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("appointmentStatuses", AppointmentStatus.values());
            
            return "rolebase/dentist/appointments";
        } else {
            // For non-dentist users, redirect to dashboard or show error
            return "redirect:/secured/dashboard";
        }
    }

    @GetMapping("/secured/patient/history")
    public String patientHistory(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User currentUser = (User) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        
        // Check if user is a dentist
        boolean isDentist = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_DENTIST"));
        
        if (isDentist) {
            // Redirect to dentist dashboard
            return "redirect:/dentalsurgeryapp/rolebase/dentist/dashboard";
        } else {
            // For other users, redirect to main dashboard
            return "redirect:/secured/dashboard";
        }
    }

    @GetMapping("/secured/patient/appointments/new")
    public String patientAppointmentsNew(Authentication authentication) {
        // This route is being removed as requested
        // Redirect users to appropriate dashboard based on their role
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User currentUser = (User) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
        
        // Check user roles and redirect appropriately
        boolean isDentist = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_DENTIST"));
        boolean isPatient = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PATIENT"));
        
        if (isDentist) {
            return "redirect:/dentalsurgeryapp/rolebase/dentist/dashboard";
        } else if (isPatient) {
            return "redirect:/dentalsurgeryapp/rolebase/patient/dashboard";
        } else {
            return "redirect:/secured/dashboard";
        }
    }

    @GetMapping("/secured/appointment/new")
    public String showNewAppointmentForm(Model model) {
        model.addAttribute(APPOINTMENT, new Appointment());
        populateReferenceData(model);
        model.addAttribute(PAGE_TITLE, ADD_NEW_APPOINTMENT_TITLE);
        return APPOINTMENT_NEW_VIEW;
    }

    @PostMapping("/secured/appointment/new")
    public String createAppointment(@ModelAttribute("appointment") Appointment appointment,
                                    @RequestParam Integer patientId,
                                    @RequestParam Integer dentistId,
                                    @RequestParam Integer surgeryLocationId,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            populateReferenceData(model);
            model.addAttribute(PAGE_TITLE, ADD_NEW_APPOINTMENT_TITLE);
            return APPOINTMENT_NEW_VIEW;
        }
        // Server-side business hours validation
        String validationErrorNew = validateBusinessHours(appointment.getAppointmentDateTime());
        if (validationErrorNew != null) {
            model.addAttribute(ERROR_MESSAGE, validationErrorNew);
            populateReferenceData(model);
            model.addAttribute(PAGE_TITLE, ADD_NEW_APPOINTMENT_TITLE);
            return APPOINTMENT_NEW_VIEW;
        }
        try {
            // Attach associations
            appointment.setPatient(patientService.getPatientById(patientId));
            appointment.setDentist(dentistService.findDentistById(dentistId).orElseThrow());
            appointment.setSurgeryLocation(surgeryLocationService.findSurgeryLocationById(surgeryLocationId).orElseThrow());

            Appointment created = appointmentService.addNewAppointment(appointment);
            ra.addFlashAttribute(SUCCESS_MESSAGE, "Appointment #" + created.getAppointmentId() + " created successfully.");
            return REDIRECT_APPOINTMENT_LIST;
        } catch (OutstandingBillException | AppointmentLimitExceededException e) {
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE, "Error creating appointment: " + e.getMessage());
        }
        populateReferenceData(model);
        model.addAttribute(PAGE_TITLE, ADD_NEW_APPOINTMENT_TITLE);
        return APPOINTMENT_NEW_VIEW;
    }

    @GetMapping("/secured/appointment/edit/{id}")
    public String showEditAppointmentForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Appointment appt = appointmentService.getAppointmentById(id);
        if (appt == null) {
            ra.addFlashAttribute("errorMessage", "Appointment not found with ID: " + id);
            return "redirect:/secured/appointment/list";
        }
        model.addAttribute("appointment", appt);
        populateReferenceData(model);
        model.addAttribute("pageTitle", "Edit Appointment");
        return "secured/appointment/edit";
    }

    @PostMapping("/secured/appointment/edit/{id}")
    public String updateAppointment(@PathVariable Integer id,
                                    @ModelAttribute("appointment") Appointment appointment,
                                    @RequestParam Integer patientId,
                                    @RequestParam Integer dentistId,
                                    @RequestParam Integer surgeryLocationId,
                                    @RequestParam(required = false) Double totalCost,
                                    @RequestParam(required = false) String paymentStatus,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            populateReferenceData(model);
            model.addAttribute("pageTitle", "Edit Appointment");
            return "secured/appointment/edit";
        }
        // Ensure the appointment retains its ID for the edit form even if validation fails later
        appointment.setAppointmentId(id);
        // Server-side business hours validation
        String validationErrorEdit = validateBusinessHours(appointment.getAppointmentDateTime());
        if (validationErrorEdit != null) {
            model.addAttribute("errorMessage", validationErrorEdit);
            populateReferenceData(model);
            model.addAttribute("pageTitle", "Edit Appointment");
            return "secured/appointment/edit";
        }
        try {
            appointment.setPatient(patientService.getPatientById(patientId));
            appointment.setDentist(dentistService.findDentistById(dentistId).orElseThrow());
            appointment.setSurgeryLocation(surgeryLocationService.findSurgeryLocationById(surgeryLocationId).orElseThrow());
            Appointment updated = appointmentService.updateAppointment(appointment);

            // Handle billing updates/creation
            try {
                // Find existing bill for this appointment (via DTOs, then load entity)
                cs489.miu.dentalsurgeryapp.dto.BillResponseDTO existingBillDto = billService.getAllBills().stream()
                    .filter(b -> b.appointment() != null
                              && b.appointment().appointmentId() != null
                              && b.appointment().appointmentId().equals(updated.getAppointmentId()))
                    .findFirst()
                    .orElse(null);

                if (existingBillDto != null) {
                    // Update existing bill's amount/status if provided
                    cs489.miu.dentalsurgeryapp.model.Bill existingBill = billService.getBillById(existingBillDto.billId());
                    if (existingBill != null) {
                        if (totalCost != null && totalCost > 0) {
                            existingBill.setTotalCost(java.math.BigDecimal.valueOf(totalCost));
                        }
                        if (paymentStatus != null && !paymentStatus.isBlank()) {
                            existingBill.setPaymentStatus(paymentStatus);
                        }
                        billService.updateBill(existingBill);
                        ra.addFlashAttribute("successMessage", "Appointment updated and bill updated successfully.");
                    } else {
                        ra.addFlashAttribute("successMessage", "Appointment updated. Related bill could not be loaded for update.");
                    }
                } else if ("CHECKOUT".equals(appointment.getAppointmentStatus()) && totalCost != null && totalCost > 0) {
                    // No existing bill; create if status is CHECKOUT and amount provided
                    cs489.miu.dentalsurgeryapp.model.Bill bill = new cs489.miu.dentalsurgeryapp.model.Bill();
                    bill.setTotalCost(java.math.BigDecimal.valueOf(totalCost));
                    bill.setPaymentStatus(paymentStatus != null && !paymentStatus.isBlank() ? paymentStatus : "PENDING");
                    bill.setPatient(updated.getPatient());
                    bill.setAppointment(updated);
                    billService.addNewBill(bill);
                    ra.addFlashAttribute("successMessage", "Appointment updated and bill created successfully.");
                } else {
                    // Regular appointment update without billing changes
                    ra.addFlashAttribute("successMessage", "Appointment #" + updated.getAppointmentId() + " updated successfully.");
                }
            } catch (Exception e) {
                ra.addFlashAttribute("successMessage", "Appointment updated, but billing update failed: " + e.getMessage());
            }
            
            return "redirect:/secured/appointment/list";
        } catch (OutstandingBillException | AppointmentLimitExceededException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating appointment: " + e.getMessage());
        }
        populateReferenceData(model);
        model.addAttribute("pageTitle", "Edit Appointment");
        return "secured/appointment/edit";
    }

    @GetMapping("/secured/appointment/view/{id}")
    public String viewAppointment(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Appointment appt = appointmentService.getAppointmentById(id);
        if (appt == null) {
            ra.addFlashAttribute("errorMessage", "Appointment not found with ID: " + id);
            return "redirect:/secured/appointment/list";
        }
        model.addAttribute("appointment", appt);
        model.addAttribute("pageTitle", "Appointment Details");
        return "secured/appointment/view";
    }

    @PostMapping("/secured/appointment/delete/{id}")
    public String deleteAppointmentUi(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            boolean deleted = appointmentService.deleteAppointmentById(id);
            if (deleted) {
                ra.addFlashAttribute("successMessage", "Appointment deleted successfully.");
            } else {
                ra.addFlashAttribute("errorMessage", "Appointment not found with ID: " + id);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error deleting appointment: " + e.getMessage());
        }
        return "redirect:/secured/appointment/list";
    }

    private void populateReferenceData(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        // Prefer ordered lists for better UX
        model.addAttribute("dentists", dentistService.getAllDentistsOrderedByName());
        model.addAttribute("surgeryLocations", surgeryLocationService.getAllSurgeryLocationsOrderedByName());
        // Back-compat aliases and static lists (if templates use them)
        model.addAttribute("locations", surgeryLocationService.getAllSurgeryLocationsOrderedByName());
        model.addAttribute("statuses", java.util.List.of("SCHEDULED", "COMPLETED", "CANCELLED"));
        model.addAttribute("types", java.util.List.of("CHECKUP", "CLEANING", "FILLING", "SURGERY"));
    }

    /**
     * Validates appointment date/time against business hours:
     * - Monday-Friday: 8:00 AM - 6:00 PM
     * - Saturday: 9:00 AM - 3:00 PM
     * - Sunday: Closed
     * Also ensures the appointment is in the future.
     *
     * @param appointmentDateTime The date and time of the appointment
     * @return Error message if validation fails; null if valid
     */
    private String validateBusinessHours(LocalDateTime appointmentDateTime) {
        if (appointmentDateTime == null) {
            return "Appointment date and time is required.";
        }

        // Must be in the future
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            return "Appointment date and time must be in the future.";
        }

        DayOfWeek dayOfWeek = appointmentDateTime.getDayOfWeek();
        int hour = appointmentDateTime.getHour();

        // Sunday closed
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            return "We are closed on Sundays. Please select another day.";
        }

        // Saturday 9 AM - 3 PM
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            if (hour < 9 || hour >= 15) {
                return "Saturday appointments are only available between 9:00 AM and 3:00 PM.";
            }
            return null;
        }

        // Monday-Friday 8 AM - 6 PM
        if (hour < 8 || hour >= 18) {
            return "Weekday appointments are only available between 8:00 AM and 6:00 PM.";
        }

        return null;
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        List<AppointmentResponseDTO> appointmentDTOs = appointments.stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(appointmentDTOs);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/appointments/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Integer id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment != null) {
            return ResponseEntity.ok(mapToDTO(appointment));
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @PostMapping("/dentalsugery/api/appointments")
    public ResponseEntity<Object> createAppointment(@RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        try {
            // Server-side business hours validation for API
            String apiValidationError = validateBusinessHours(appointmentRequestDTO.getAppointmentDateTime());
            if (apiValidationError != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Invalid Appointment Time", apiValidationError));
            }
            Appointment appointment = mapToEntity(appointmentRequestDTO);
            Appointment createdAppointment = appointmentService.addNewAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdAppointment));
        } catch (OutstandingBillException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Outstanding Bills", e.getMessage()));
        } catch (AppointmentLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Appointment Limit Exceeded", e.getMessage()));
        } catch (Exception _) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Bad Request", "Invalid appointment data provided"));
        }
    }

    @ResponseBody
    @PutMapping("/dentalsugery/api/appointments/{id}")
    public ResponseEntity<Object> updateAppointment(@PathVariable Integer id, @RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        try {
            // Verify the appointment exists first
            appointmentService.getAppointmentById(id);
            // Server-side business hours validation for API
            String apiValidationError = validateBusinessHours(appointmentRequestDTO.getAppointmentDateTime());
            if (apiValidationError != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Invalid Appointment Time", apiValidationError));
            }
            
            Appointment appointment = mapToEntity(appointmentRequestDTO);
            appointment.setAppointmentId(id);
            Appointment updatedAppointment = appointmentService.updateAppointment(appointment);
            return ResponseEntity.ok(mapToDTO(updatedAppointment));
        } catch (OutstandingBillException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Outstanding Bills", e.getMessage()));
        } catch (AppointmentLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Appointment Limit Exceeded", e.getMessage()));
        } catch (Exception _) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Bad Request", "Invalid appointment data or appointment not found"));
        }
    }

    @ResponseBody
    @DeleteMapping("/dentalsugery/api/appointments/{id}")
    public ResponseEntity<DeleteResponseDTO> deleteAppointment(@PathVariable Integer id) {
        boolean deleted = appointmentService.deleteAppointmentById(id);
        if (deleted) {
            DeleteResponseDTO response = new DeleteResponseDTO(true, "Appointment with ID " + id + " has been successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            DeleteResponseDTO response = new DeleteResponseDTO(false, "Appointment with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private AppointmentResponseDTO mapToDTO(Appointment appointment) {
        // Map Patient to PatientResponseDTO
        PatientResponseDTO patientResponseDTO = null;
        if (appointment.getPatient() != null) {
            Patient patient = appointment.getPatient();
            AddressResponseDTO addressDTO = null;
            if (patient.getAddress() != null) {
                Address address = patient.getAddress();
                addressDTO = new AddressResponseDTO(
                        address.getAddressId(),
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getZipcode()
                );
            }
            patientResponseDTO = new PatientResponseDTO(
                    patient.getPatientId(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    patient.getContactNumber(),
                    patient.getEmail(),
                    patient.getDob(),
                    addressDTO
            );
        }

        // Map Dentist to DentistResponseDTO
        DentistResponseDTO dentistResponseDTO = null;
        if (appointment.getDentist() != null) {
            Dentist dentist = appointment.getDentist();
            dentistResponseDTO = new DentistResponseDTO(
                    dentist.getDentistId(),
                    dentist.getFirstName(),
                    dentist.getLastName(),
                    dentist.getContactNumber(),
                    dentist.getEmail(),
                    dentist.getSpecialization()
            );
        }

        // Map SurgeryLocation to SurgeryLocationResponseDTO
        SurgeryLocationResponseDTO surgeryLocationResponseDTO = null;
        if (appointment.getSurgeryLocation() != null) {
            SurgeryLocation location = appointment.getSurgeryLocation();
            AddressResponseDTO locationAddressDTO = null;
            if (location.getLocation() != null) {
                Address address = location.getLocation();
                locationAddressDTO = new AddressResponseDTO(
                        address.getAddressId(),
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getZipcode()
                );
            }
            surgeryLocationResponseDTO = new SurgeryLocationResponseDTO(
                    location.getSurgeryLocationId(),
                    location.getName(),
                    location.getContactNumber(),
                    locationAddressDTO
            );
        }

        return new AppointmentResponseDTO(
                appointment.getAppointmentId(),
                appointment.getAppointmentType(),
                appointment.getAppointmentStatus(),
                appointment.getAppointmentDateTime(),
                patientResponseDTO,
                dentistResponseDTO,
                surgeryLocationResponseDTO
        );
    }

    private Appointment mapToEntity(AppointmentRequestDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentType(dto.getAppointmentType());
        appointment.setAppointmentStatus(dto.getAppointmentStatus());
        appointment.setAppointmentDateTime(dto.getAppointmentDateTime());

        // Create and set Patient
        if (dto.getPatientRequestDTO() != null) {
            Patient patient = new Patient();
            patient.setFirstName(dto.getPatientRequestDTO().firstName());
            patient.setLastName(dto.getPatientRequestDTO().lastName());
            patient.setContactNumber(dto.getPatientRequestDTO().contactNumber());
            patient.setEmail(dto.getPatientRequestDTO().email());
            patient.setDob(dto.getPatientRequestDTO().dob());
            
            // Create and set Address for Patient
            if (dto.getPatientRequestDTO().addressRequestDTO() != null) {
                Address address = new Address();
                address.setStreet(dto.getPatientRequestDTO().addressRequestDTO().street());
                address.setCity(dto.getPatientRequestDTO().addressRequestDTO().city());
                address.setState(dto.getPatientRequestDTO().addressRequestDTO().state());
                address.setZipcode(dto.getPatientRequestDTO().addressRequestDTO().zipcode());
                address = addressService.findOrCreateAddress(address);
                patient.setAddress(address);
            }
            
            patient = patientService.addNewPatient(patient);
            appointment.setPatient(patient);
        }

        // Create and set Dentist
        if (dto.getDentistRequestDTO() != null) {
            Dentist dentist = new Dentist();
            dentist.setFirstName(dto.getDentistRequestDTO().firstName());
            dentist.setLastName(dto.getDentistRequestDTO().lastName());
            dentist.setContactNumber(dto.getDentistRequestDTO().contactNumber());
            dentist.setEmail(dto.getDentistRequestDTO().email());
            dentist.setSpecialization(dto.getDentistRequestDTO().specialization());
            
            dentist = dentistService.saveDentist(dentist);
            appointment.setDentist(dentist);
        }

        // Create and set SurgeryLocation
        if (dto.getSurgeryLocationRequestDTO() != null) {
            SurgeryLocation location = new SurgeryLocation();
            location.setName(dto.getSurgeryLocationRequestDTO().name());
            location.setContactNumber(dto.getSurgeryLocationRequestDTO().contactNumber());
            
            // Create and set Address for SurgeryLocation
            if (dto.getSurgeryLocationRequestDTO().addressRequestDTO() != null) {
                Address address = new Address();
                address.setStreet(dto.getSurgeryLocationRequestDTO().addressRequestDTO().street());
                address.setCity(dto.getSurgeryLocationRequestDTO().addressRequestDTO().city());
                address.setState(dto.getSurgeryLocationRequestDTO().addressRequestDTO().state());
                address.setZipcode(dto.getSurgeryLocationRequestDTO().addressRequestDTO().zipcode());
                address = addressService.findOrCreateAddress(address);
                location.setLocation(address);
            }
            
            location = surgeryLocationService.saveSurgeryLocation(location);
            appointment.setSurgeryLocation(location);
        }

        return appointment;
    }
}
