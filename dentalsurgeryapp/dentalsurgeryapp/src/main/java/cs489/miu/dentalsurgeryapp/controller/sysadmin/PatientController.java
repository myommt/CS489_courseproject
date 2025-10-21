package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cs489.miu.dentalsurgeryapp.dto.AddressRequestDTO;
import cs489.miu.dentalsurgeryapp.dto.AddressResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.DeleteResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.PatientRequestDTO;
import cs489.miu.dentalsurgeryapp.dto.PatientResponseDTO;
import cs489.miu.dentalsurgeryapp.exception.PatientNotFoundException;
import cs489.miu.dentalsurgeryapp.model.Address;
import cs489.miu.dentalsurgeryapp.model.Patient;
import cs489.miu.dentalsurgeryapp.service.PatientService;

import jakarta.validation.Valid;

/**
 * Unified Patient Controller
 * - Serves MVC pages under /secured/patient
 * - Exposes REST API under /dentalsugery/api/patients
 */
@Controller("patientController")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // ===================== MVC (Thymeleaf) endpoints =====================

    /** Display list of all patients */
    @GetMapping({"/secured/patient/", "/secured/patient/list"})
    public String listPatients(Model model) {
        try {
            List<PatientResponseDTO> patients = patientService.getAllPatientsSortedByLastName();
            model.addAttribute("patients", patients);
            model.addAttribute("pageTitle", "Patient List");
            return "secured/patient/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading patients: " + e.getMessage());
            return "secured/patient/list";
        }
    }

    /** Show form for adding new patient */
    @GetMapping("/secured/patient/new")
    public String showNewPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("pageTitle", "Add New Patient");
        return "secured/patient/new";
    }

    /** Process new patient form submission */
    @PostMapping("/secured/patient/new")
    public String createPatient(@Valid @ModelAttribute("patient") Patient patient,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Add New Patient");
            return "secured/patient/new";
        }

        try {
            Patient savedPatient = patientService.addNewPatient(patient);
            redirectAttributes.addFlashAttribute("successMessage",
                "Patient " + savedPatient.getFirstName() + " " + savedPatient.getLastName() + " has been successfully added.");
            return "redirect:/secured/patient/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving patient: " + e.getMessage());
            model.addAttribute("pageTitle", "Add New Patient");
            return "secured/patient/new";
        }
    }

    /** Show form for editing existing patient */
    @GetMapping("/secured/patient/edit/{id}")
    public String showEditPatientForm(@PathVariable Integer id, Model model) {
        try {
            Patient patient = patientService.getPatientById(id);
            model.addAttribute("patient", patient);
            model.addAttribute("pageTitle", "Edit Patient");
            return "secured/patient/edit";
        } catch (PatientNotFoundException e) {
            model.addAttribute("errorMessage", "Patient not found with ID: " + id);
            return "redirect:/secured/patient/list";
        }
    }

    /** Process patient edit form submission */
    @PostMapping("/secured/patient/edit/{id}")
    public String updatePatient(@PathVariable Integer id,
                               @Valid @ModelAttribute("patient") Patient patient,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Patient");
            return "secured/patient/edit";
        }

        try {
            patient.setPatientId(id);
            Patient updatedPatient = patientService.updatePatient(patient);
            redirectAttributes.addFlashAttribute("successMessage",
                "Patient " + updatedPatient.getFirstName() + " " + updatedPatient.getLastName() + " has been successfully updated.");
            return "redirect:/secured/patient/list";
        } catch (PatientNotFoundException e) {
            model.addAttribute("errorMessage", "Patient not found with ID: " + id);
            return "redirect:/secured/patient/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating patient: " + e.getMessage());
            model.addAttribute("pageTitle", "Edit Patient");
            return "secured/patient/edit";
        }
    }

    /** View patient details */
    @GetMapping("/secured/patient/view/{id}")
    public String viewPatient(@PathVariable Integer id, Model model) {
        try {
            Patient patient = patientService.getPatientById(id);
            model.addAttribute("patient", patient);
            model.addAttribute("pageTitle", "Patient Details");
            return "secured/patient/view";
        } catch (PatientNotFoundException e) {
            model.addAttribute("errorMessage", "Patient not found with ID: " + id);
            return "redirect:/secured/patient/list";
        }
    }

    /** Delete patient (from UI) */
    @PostMapping("/secured/patient/delete/{id}")
    public String deletePatient(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = patientService.deletePatientById(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Patient has been successfully deleted.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Patient not found with ID: " + id);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting patient: " + e.getMessage());
        }
        return "redirect:/secured/patient/list";
    }

    /** Unified search - show search form and handle search results */
    @GetMapping("/secured/patient/search")
    public String searchPatients(@RequestParam(required = false) String searchTerm,
                                @RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String lastName,
                                @RequestParam(required = false) String contactNumber,
                                @RequestParam(required = false) String email,
                                Model model) {

        boolean hasSearchParams = (searchTerm != null && !searchTerm.trim().isEmpty()) ||
                                 (firstName != null && !firstName.trim().isEmpty()) ||
                                 (lastName != null && !lastName.trim().isEmpty()) ||
                                 (contactNumber != null && !contactNumber.trim().isEmpty()) ||
                                 (email != null && !email.trim().isEmpty());

        if (hasSearchParams) {
            try {
                String finalSearchTerm = searchTerm != null && !searchTerm.trim().isEmpty() ? searchTerm :
                                        (lastName != null && !lastName.trim().isEmpty() ? lastName :
                                        (firstName != null && !firstName.trim().isEmpty() ? firstName : ""));

                List<Patient> searchResults;
                if (!finalSearchTerm.isEmpty()) {
                    searchResults = patientService.searchPatients(finalSearchTerm);
                } else {
                    searchResults = List.of();
                }

                model.addAttribute("searchResults", searchResults);
                model.addAttribute("searchPerformed", true);
                model.addAttribute("searchTerm", finalSearchTerm);

            } catch (Exception e) {
                model.addAttribute("errorMessage", "Error performing search: " + e.getMessage());
                model.addAttribute("searchResults", List.of());
                model.addAttribute("searchPerformed", true);
            }
        } else {
            model.addAttribute("searchPerformed", false);
        }

        return "secured/patient/search-result";
    }

    // ===================== REST API endpoints =====================

    @ResponseBody
    @GetMapping("/dentalsugery/api/patients")
    public ResponseEntity<List<PatientResponseDTO>> apiGetAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @ResponseBody
    @PostMapping("/dentalsugery/api/patients")
    public ResponseEntity<PatientResponseDTO> apiCreatePatient(@RequestBody PatientRequestDTO patientRequestDTO) {
        Patient patient = mapToEntity(patientRequestDTO);
        Patient createdPatient = patientService.addNewPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdPatient));
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/patients/{id}")
    public ResponseEntity<PatientResponseDTO> apiGetPatientById(@PathVariable Integer id) {
        try {
            Patient patient = patientService.getPatientById(id);
            return ResponseEntity.ok(mapToDTO(patient));
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ResponseBody
    @PutMapping("/dentalsugery/api/patients/{id}")
    public ResponseEntity<PatientResponseDTO> apiUpdatePatient(@PathVariable Integer id, @RequestBody PatientRequestDTO patientRequestDTO) {
        try {
            Patient patient = mapToEntity(patientRequestDTO);
            patient.setPatientId(id);

            Patient updatedPatient = patientService.updatePatient(patient);
            return ResponseEntity.ok(mapToDTO(updatedPatient));
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ResponseBody
    @DeleteMapping("/dentalsugery/api/patients/{id}")
    public ResponseEntity<DeleteResponseDTO> apiDeletePatient(@PathVariable Integer id) {
        boolean deleted = patientService.deletePatientById(id);
        if (deleted) {
            DeleteResponseDTO response = new DeleteResponseDTO(true, "Patient with ID " + id + " has been successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            DeleteResponseDTO response = new DeleteResponseDTO(false, "Patient with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/patients/search/{searchString}")
    public ResponseEntity<List<PatientResponseDTO>> apiSearchPatients(@PathVariable String searchString) {
        List<Patient> patients = patientService.searchPatients(searchString);
        List<PatientResponseDTO> patientDTOs = patients.stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(patientDTOs);
    }

    // ===================== Mapping helpers =====================

    private PatientResponseDTO mapToDTO(Patient patient) {
        AddressResponseDTO addressDTO = null;
        if (patient.getAddress() != null) {
            addressDTO = new AddressResponseDTO(
                    patient.getAddress().getAddressId(),
                    patient.getAddress().getStreet(),
                    patient.getAddress().getCity(),
                    patient.getAddress().getState(),
                    patient.getAddress().getZipcode()
            );
        }

        return new PatientResponseDTO(
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getContactNumber(),
                patient.getEmail(),
                patient.getDob(),
                addressDTO
        );
    }

    private Patient mapToEntity(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setFirstName(patientRequestDTO.firstName());
        patient.setLastName(patientRequestDTO.lastName());
        patient.setContactNumber(patientRequestDTO.contactNumber());
        patient.setEmail(patientRequestDTO.email());
        patient.setDob(patientRequestDTO.dob());

        if (patientRequestDTO.addressRequestDTO() != null) {
            var addressDTO = patientRequestDTO.addressRequestDTO();
            if (isValidAddressData(addressDTO)) {
                Address address = new Address();
                address.setStreet(addressDTO.street());
                address.setCity(addressDTO.city());
                address.setState(addressDTO.state());
                address.setZipcode(addressDTO.zipcode());
                patient.setAddress(address);
            }
        }

        return patient;
    }

    private boolean isValidAddressData(AddressRequestDTO addressDTO) {
        return addressDTO.street() != null && !addressDTO.street().trim().isEmpty() &&
               addressDTO.city() != null && !addressDTO.city().trim().isEmpty() &&
               addressDTO.state() != null && !addressDTO.state().trim().isEmpty() &&
               addressDTO.zipcode() != null && !addressDTO.zipcode().trim().isEmpty();
    }
}
