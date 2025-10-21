package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cs489.miu.dentalsurgeryapp.dto.SurgeryLocationResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.AddressResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.DeleteResponseDTO;
import cs489.miu.dentalsurgeryapp.model.SurgeryLocation;
import cs489.miu.dentalsurgeryapp.service.SurgeryLocationService;

/**
 * Unified Surgery Location Controller
 * - MVC pages under /secured/location
 * - REST API under /dentalsugery/api/surgerylocations
 */
@Controller("surgeryLocationController")
public class SurgeryLocationController {

    private final SurgeryLocationService surgeryLocationService;

    public SurgeryLocationController(SurgeryLocationService surgeryLocationService) {
        this.surgeryLocationService = surgeryLocationService;
    }

    // ===================== MVC (Thymeleaf) endpoints =====================

    @GetMapping({"/secured/location/", "/secured/location/list"})
    public String listLocations(Model model) {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocationsOrderedByName();
        model.addAttribute("locations", surgeryLocations);
        model.addAttribute("pageTitle", "Surgery Location List");
        return "secured/location/list";
    }

    @GetMapping("/secured/location/new")
    public String showNewLocationForm(Model model) {
        SurgeryLocation loc = new SurgeryLocation();
        // Initialize nested address for clean form binding
        loc.setLocation(new cs489.miu.dentalsurgeryapp.model.Address());
        model.addAttribute("location", loc);
        model.addAttribute("pageTitle", "Add New Location");
        return "secured/location/new";
    }

    @PostMapping("/secured/location/new")
    public String createLocation(@ModelAttribute("location") SurgeryLocation location,
                                 RedirectAttributes ra) {
        surgeryLocationService.saveSurgeryLocation(location);
        ra.addFlashAttribute("successMessage", "Location has been added.");
        return "redirect:/secured/location/list";
    }

    @GetMapping("/secured/location/edit/{id}")
    public String showEditLocationForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<SurgeryLocation> loc = surgeryLocationService.findSurgeryLocationById(id);
        if (loc.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Location not found with ID: " + id);
            return "redirect:/secured/location/list";
        }
        SurgeryLocation loaded = loc.get();
        if (loaded.getLocation() == null) {
            loaded.setLocation(new cs489.miu.dentalsurgeryapp.model.Address());
        }
        model.addAttribute("location", loaded);
        model.addAttribute("pageTitle", "Edit Location");
        return "secured/location/edit";
    }

    @PostMapping("/secured/location/edit/{id}")
    public String updateLocation(@PathVariable Integer id,
                                 @ModelAttribute("location") SurgeryLocation location,
                                 RedirectAttributes ra) {
        location.setSurgeryLocationId(id);
        surgeryLocationService.updateSurgeryLocation(id, location);
        ra.addFlashAttribute("successMessage", "Location has been updated.");
        return "redirect:/secured/location/list";
    }

    @GetMapping("/secured/location/view/{id}")
    public String viewLocation(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<SurgeryLocation> loc = surgeryLocationService.findSurgeryLocationById(id);
        if (loc.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Location not found with ID: " + id);
            return "redirect:/secured/location/list";
        }
        model.addAttribute("location", loc.get());
        model.addAttribute("pageTitle", "Location Details");
        return "secured/location/view";
    }

    /** Delete location (from UI) */
    @PostMapping("/secured/location/delete/{id}")
    public String deleteLocationUi(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            boolean deleted = surgeryLocationService.deleteSurgeryLocationById(id);
            if (deleted) {
                ra.addFlashAttribute("successMessage", "Surgery location deleted successfully.");
            } else {
                ra.addFlashAttribute("errorMessage", "Surgery location not found with ID: " + id);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error deleting location: " + e.getMessage());
        }
        return "redirect:/secured/location/list";
    }

    @GetMapping("/secured/location/search")
    public String searchLocations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String zipcode,
            Model model) {

        // naive multi-field filter using existing service methods when applicable
        List<SurgeryLocation> results;
        boolean anyParam = (name != null && !name.isBlank()) || (contactNumber != null && !contactNumber.isBlank())
                || (city != null && !city.isBlank()) || (state != null && !state.isBlank())
                || (zipcode != null && !zipcode.isBlank());

        if (!anyParam) {
            results = List.of();
        } else if (name != null && !name.isBlank()) {
            Optional<SurgeryLocation> byName = surgeryLocationService.findSurgeryLocationByName(name);
            results = byName.map(List::of).orElseGet(List::of);
        } else if (contactNumber != null && !contactNumber.isBlank()) {
            results = surgeryLocationService.findSurgeryLocationsByContactNumber(contactNumber);
        } else if (city != null && !city.isBlank()) {
            results = surgeryLocationService.findSurgeryLocationsByCity(city);
        } else if (state != null && !state.isBlank()) {
            results = surgeryLocationService.findSurgeryLocationsByState(state);
        } else { // zipcode
            results = surgeryLocationService.findSurgeryLocationsByZipcode(zipcode);
        }

        model.addAttribute("searchPerformed", anyParam);
        model.addAttribute("searchResults", results);
        model.addAttribute("pageTitle", "Search Surgery Locations");
        return "secured/location/search-result";
    }

    // ===================== REST API endpoints =====================

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getAllSurgeryLocations() {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/ordered")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getAllSurgeryLocationsOrderedByName() {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocationsOrderedByName();
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/{id}")
    public ResponseEntity<SurgeryLocationResponseDTO> getSurgeryLocationById(@PathVariable Integer id) {
        Optional<SurgeryLocation> surgeryLocation = surgeryLocationService.findSurgeryLocationById(id);
        return surgeryLocation.map(location -> ResponseEntity.ok(mapToDTO(location)))
                .orElse(ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/name/{name}")
    public ResponseEntity<SurgeryLocationResponseDTO> getSurgeryLocationByName(@PathVariable String name) {
        Optional<SurgeryLocation> surgeryLocation = surgeryLocationService.findSurgeryLocationByName(name);
        return surgeryLocation.map(location -> ResponseEntity.ok(mapToDTO(location)))
                .orElse(ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/contact/{contactNumber}")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getSurgeryLocationsByContactNumber(@PathVariable String contactNumber) {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.findSurgeryLocationsByContactNumber(contactNumber);
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/city/{city}")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getSurgeryLocationsByCity(@PathVariable String city) {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.findSurgeryLocationsByCity(city);
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/state/{state}")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getSurgeryLocationsByState(@PathVariable String state) {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.findSurgeryLocationsByState(state);
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/zipcode/{zipcode}")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getSurgeryLocationsByZipcode(@PathVariable String zipcode) {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.findSurgeryLocationsByZipcode(zipcode);
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @ResponseBody
    @PostMapping("/dentalsugery/api/surgerylocations")
    public ResponseEntity<SurgeryLocationResponseDTO> createSurgeryLocation(@RequestBody SurgeryLocation surgeryLocation) {
        SurgeryLocation createdSurgeryLocation = surgeryLocationService.saveSurgeryLocation(surgeryLocation);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdSurgeryLocation));
    }

    @ResponseBody
    @PutMapping("/dentalsugery/api/surgerylocations/{id}")
    public ResponseEntity<SurgeryLocationResponseDTO> updateSurgeryLocation(@PathVariable Integer id, @RequestBody SurgeryLocation surgeryLocation) {
        Optional<SurgeryLocation> updatedSurgeryLocation = surgeryLocationService.updateSurgeryLocation(id, surgeryLocation);
        return updatedSurgeryLocation.map(location -> ResponseEntity.ok(mapToDTO(location)))
                .orElse(ResponseEntity.notFound().build());
    }

    @ResponseBody
    @DeleteMapping("/dentalsugery/api/surgerylocations/{id}")
    public ResponseEntity<DeleteResponseDTO> deleteSurgeryLocation(@PathVariable Integer id) {
        boolean deleted = surgeryLocationService.deleteSurgeryLocationById(id);
        if (deleted) {
            DeleteResponseDTO response = new DeleteResponseDTO(true, "Surgery Location with ID " + id + " has been successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            DeleteResponseDTO response = new DeleteResponseDTO(false, "Surgery Location with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Integer id) {
        boolean exists = surgeryLocationService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        boolean exists = surgeryLocationService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/surgerylocations/count")
    public ResponseEntity<Long> getTotalSurgeryLocationCount() {
        long count = surgeryLocationService.getTotalSurgeryLocationCount();
        return ResponseEntity.ok(count);
    }

    private SurgeryLocationResponseDTO mapToDTO(SurgeryLocation surgeryLocation) {
        AddressResponseDTO addressDTO = null;
        if (surgeryLocation.getLocation() != null) {
            addressDTO = new AddressResponseDTO(
                    surgeryLocation.getLocation().getAddressId(),
                    surgeryLocation.getLocation().getStreet(),
                    surgeryLocation.getLocation().getCity(),
                    surgeryLocation.getLocation().getState(),
                    surgeryLocation.getLocation().getZipcode()
            );
        }

        return new SurgeryLocationResponseDTO(
                surgeryLocation.getSurgeryLocationId(),
                surgeryLocation.getName(),
                surgeryLocation.getContactNumber(),
                addressDTO
        );
    }
}
