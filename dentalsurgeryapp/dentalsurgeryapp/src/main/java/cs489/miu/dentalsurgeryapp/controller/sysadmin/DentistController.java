    package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cs489.miu.dentalsurgeryapp.dto.DentistResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.DeleteResponseDTO;
import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.service.DentistService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Unified Dentist Controller
 * - Serves MVC pages under /secured/dentist
 * - Exposes REST API under /dentalsugery/api/dentists
 */
@Controller("dentistController")
public class DentistController {

    private final DentistService dentistService;

    public DentistController(DentistService dentistService) {
        this.dentistService = dentistService;
    }

    // ===================== MVC (Thymeleaf) endpoints =====================

    @GetMapping({"/secured/dentist/", "/secured/dentist/list"})
    public String listDentists(@RequestParam(required = false) String searchTerm, Model model) {
        List<Dentist> dentists;
        try {
            dentists = dentistService.getAllDentistsOrderedByName();
        } catch (Exception ignored) {
            dentists = dentistService.getAllDentists();
        }
        
        // Filter by searchTerm if provided
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String searchLower = searchTerm.trim().toLowerCase();
            dentists = dentists.stream()
                .filter(d -> 
                    (d.getFirstName() != null && d.getFirstName().toLowerCase().contains(searchLower)) ||
                    (d.getLastName() != null && d.getLastName().toLowerCase().contains(searchLower)) ||
                    (d.getEmail() != null && d.getEmail().toLowerCase().contains(searchLower)) ||
                    (d.getSpecialization() != null && d.getSpecialization().toLowerCase().contains(searchLower))
                )
                .collect(Collectors.toList());
        }
        
        model.addAttribute("dentists", dentists);
        model.addAttribute("pageTitle", "Dentist List");
        model.addAttribute("searchTerm", searchTerm);
        return "secured/dentist/list";
    }

    @GetMapping("/secured/dentist/new")
    public String showNewDentistForm(Model model) {
        model.addAttribute("dentist", new Dentist());
        model.addAttribute("pageTitle", "Add New Dentist");
        return "secured/dentist/new";
    }

    @PostMapping("/secured/dentist/new")
    public String createDentist(@Valid @ModelAttribute("dentist") Dentist dentist,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Add New Dentist");
            return "secured/dentist/new";
        }
        Dentist saved = dentistService.saveDentist(dentist);
    redirectAttributes.addFlashAttribute("successMessage",
        "Dentist " + saved.getFirstName() + " " + saved.getLastName() + " has been added.");
        return "redirect:/secured/dentist/list";
    }

    @GetMapping("/secured/dentist/edit/{id}")
    public String showEditDentistForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Dentist> dentist = dentistService.findDentistById(id);
        if (dentist.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Dentist not found with ID: " + id);
            return "redirect:/secured/dentist/list";
        }
        model.addAttribute("dentist", dentist.get());
        model.addAttribute("pageTitle", "Edit Dentist");
        return "secured/dentist/edit";
    }

    @PostMapping("/secured/dentist/edit/{id}")
    public String updateDentist(@PathVariable Integer id,
                                @Valid @ModelAttribute("dentist") Dentist dentist,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Dentist");
            return "secured/dentist/edit";
        }
        dentist.setDentistId(id);
        dentistService.saveDentist(dentist);
    redirectAttributes.addFlashAttribute("successMessage",
        "Dentist has been updated.");
        return "redirect:/secured/dentist/list";
    }

    @GetMapping("/secured/dentist/view/{id}")
    public String viewDentist(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Dentist> dentist = dentistService.findDentistById(id);
        if (dentist.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Dentist not found with ID: " + id);
            return "redirect:/secured/dentist/list";
        }
        model.addAttribute("dentist", dentist.get());
        model.addAttribute("pageTitle", "Dentist Details");
        return "secured/dentist/view";
    }

    @PostMapping("/secured/dentist/delete/{id}")
    public String deleteDentistUi(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            boolean deleted = dentistService.deleteDentistById(id);
            if (deleted) {
                ra.addFlashAttribute("successMessage", "Dentist deleted.");
            } else {
                ra.addFlashAttribute("errorMessage", "Dentist not found with ID: " + id);
            }
        } catch (DataIntegrityViolationException ex) {
            // Friendly message for FK constraint violations (e.g., existing appointments)
            ra.addFlashAttribute(
                "errorMessage",
                "Cannot delete this dentist because they have existing appointments. " +
                "Please delete or reassign those appointments first."
            );
        } catch (Exception ignored) {
            // Generic fallback message (avoid exposing technical details)
            ra.addFlashAttribute(
                "errorMessage",
                "Unable to delete the dentist at the moment. Please try again or contact support."
            );
        }
        return "redirect:/secured/dentist/list";
    }

    // Fallback endpoint if form posts without path variable (e.g., JS didn't set action)
    @PostMapping("/secured/dentist/delete")
    public String deleteDentistUiParam(@RequestParam(value = "id", required = false) Integer id, RedirectAttributes ra) {
        if (id == null) {
            ra.addFlashAttribute("errorMessage", "Missing dentist id for deletion.");
            return "redirect:/secured/dentist/list";
        }
        return deleteDentistUi(id, ra);
    }

    // Guard direct GET access to /secured/dentist/delete
    @GetMapping("/secured/dentist/delete")
    public String deleteDentistGetGuard(RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", "Invalid request: no dentist selected for deletion.");
        return "redirect:/secured/dentist/list";
    }

    // ===================== REST API endpoints =====================

    @ResponseBody
    @GetMapping("/dentalsugery/api/dentists")
    public ResponseEntity<List<DentistResponseDTO>> getAllDentistsApi() {
        List<Dentist> dentists = dentistService.getAllDentists();
        List<DentistResponseDTO> dentistDTOs = dentists.stream()
        .map(this::mapToDTO)
        .toList();
        return ResponseEntity.ok(dentistDTOs);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/dentists/{id}")
    public ResponseEntity<DentistResponseDTO> getDentistByIdApi(@PathVariable Integer id) {
        Optional<Dentist> dentist = dentistService.findDentistById(id);
        return dentist.map(d -> ResponseEntity.ok(mapToDTO(d)))
                .orElse(ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/dentists/email/{email}")
    public ResponseEntity<DentistResponseDTO> getDentistByEmailApi(@PathVariable String email) {
        Optional<Dentist> dentist = dentistService.findDentistByEmail(email);
        return dentist.map(d -> ResponseEntity.ok(mapToDTO(d)))
                .orElse(ResponseEntity.notFound().build());
    }

    @ResponseBody
    @PostMapping("/dentalsugery/api/dentists")
    public ResponseEntity<DentistResponseDTO> createDentistApi(@RequestBody Dentist dentist) {
        Dentist createdDentist = dentistService.saveDentist(dentist);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdDentist));
    }

    @ResponseBody
    @PutMapping("/dentalsugery/api/dentists/{id}")
    public ResponseEntity<DentistResponseDTO> updateDentistApi(@PathVariable Integer id, @RequestBody Dentist dentist) {
        dentist.setDentistId(id);
        Dentist updatedDentist = dentistService.saveDentist(dentist);
        return ResponseEntity.ok(mapToDTO(updatedDentist));
    }

    @ResponseBody
    @DeleteMapping("/dentalsugery/api/dentists/{id}")
    public ResponseEntity<DeleteResponseDTO> deleteDentistApi(@PathVariable Integer id) {
        try {
            boolean deleted = dentistService.deleteDentistById(id);
            if (deleted) {
                DeleteResponseDTO response = new DeleteResponseDTO(true, "Dentist with ID " + id + " has been successfully deleted.");
                return ResponseEntity.ok(response);
            } else {
                DeleteResponseDTO response = new DeleteResponseDTO(false, "Dentist with ID " + id + " not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (DataIntegrityViolationException ex) {
            DeleteResponseDTO response = new DeleteResponseDTO(
                false,
                "Cannot delete this dentist because they have existing appointments. Please delete or reassign those appointments first."
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception ignored) {
            DeleteResponseDTO response = new DeleteResponseDTO(
                false,
                "Unable to delete the dentist at the moment. Please try again or contact support."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private DentistResponseDTO mapToDTO(Dentist dentist) {
        return new DentistResponseDTO(
                dentist.getDentistId(),
                dentist.getFirstName(),
                dentist.getLastName(),
                dentist.getContactNumber(),
                dentist.getEmail(),
                dentist.getSpecialization()
        );
    }
}
