package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cs489.miu.dentalsurgeryapp.dto.AddressResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.AddressWithPatientsResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.DeleteResponseDTO;
import cs489.miu.dentalsurgeryapp.model.Address;
import cs489.miu.dentalsurgeryapp.service.AddressService;
import jakarta.validation.Valid;

/**
 * Unified Address Controller
 * - MVC pages under /secured/address
 * - REST API under /dentalsugery/api/addresses
 */
@Controller("addressController")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // ===================== MVC (Thymeleaf) endpoints =====================

    @GetMapping({"/secured/address/", "/secured/address/list"})
    public String listAddresses(@RequestParam(required = false) String searchTerm, Model model) {
        List<Address> addresses = addressService.getAllAddresses();
        
        // Filter by searchTerm if provided
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String searchLower = searchTerm.trim().toLowerCase();
            addresses = addresses.stream()
                .filter(a -> 
                    (a.getStreet() != null && a.getStreet().toLowerCase().contains(searchLower)) ||
                    (a.getCity() != null && a.getCity().toLowerCase().contains(searchLower)) ||
                    (a.getState() != null && a.getState().toLowerCase().contains(searchLower)) ||
                    (a.getZipcode() != null && a.getZipcode().toLowerCase().contains(searchLower))
                )
                .collect(java.util.stream.Collectors.toList());
        }
        
        model.addAttribute("addresses", addresses);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("pageTitle", "Address List");
        return "secured/address/list";
    }

    @GetMapping("/secured/address/search")
    public String searchAddresses(@RequestParam(required = false) String street,
                                  @RequestParam(required = false) String city,
                                  @RequestParam(required = false) String state,
                                  @RequestParam(required = false) String zipcode,
                                  Model model) {
        // Determine if any search criteria was provided
        boolean hasCriteria = (street != null && !street.isBlank()) ||
                              (city != null && !city.isBlank()) ||
                              (state != null && !state.isBlank()) ||
                              (zipcode != null && !zipcode.isBlank());

        model.addAttribute("pageTitle", hasCriteria ? "Address Search Results" : "Search Addresses");
        model.addAttribute("searchPerformed", hasCriteria);

        if (!hasCriteria) {
            // First visit: show the search form with no results
            return "secured/address/search-result";
        }

        String streetQ = street == null ? null : street.trim().toLowerCase();
        String cityQ = city == null ? null : city.trim().toLowerCase();
        String stateQ = state == null ? null : state.trim().toLowerCase();
        String zipQ = zipcode == null ? null : zipcode.trim().toLowerCase();

        List<Address> results = addressService.getAllAddresses().stream()
            .filter(a -> {
                if (streetQ != null && !streetQ.isBlank()) {
                    String val = a.getStreet();
                    if (val == null || !val.toLowerCase().contains(streetQ)) return false;
                }
                if (cityQ != null && !cityQ.isBlank()) {
                    String val = a.getCity();
                    if (val == null || !val.toLowerCase().contains(cityQ)) return false;
                }
                if (stateQ != null && !stateQ.isBlank()) {
                    String val = a.getState();
                    if (val == null || !val.toLowerCase().contains(stateQ)) return false;
                }
                if (zipQ != null && !zipQ.isBlank()) {
                    String val = a.getZipcode();
                    if (val == null || !val.toLowerCase().contains(zipQ)) return false;
                }
                return true;
            })
            .collect(java.util.stream.Collectors.toList());

        model.addAttribute("searchResults", results);
        return "secured/address/search-result";
    }

    @GetMapping("/secured/address/new")
    public String showNewAddressForm(Model model) {
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Add New Address");
        return "secured/address/new";
    }

    @PostMapping("/secured/address/new")
    public String createAddressUi(@Valid @ModelAttribute("address") Address address,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Add New Address");
            return "secured/address/new";
        }
    addressService.addNewAddress(address);
        ra.addFlashAttribute("successMessage", "Address has been added.");
        return "redirect:/secured/address/list";
    }

    @GetMapping("/secured/address/edit/{id}")
    public String showEditAddressForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            ra.addFlashAttribute("errorMessage", "Address not found with ID: " + id);
            return "redirect:/secured/address/list";
        }
        model.addAttribute("address", address);
        model.addAttribute("pageTitle", "Edit Address");
        return "secured/address/edit";
    }

    @PostMapping("/secured/address/edit/{id}")
    public String updateAddressUi(@PathVariable Integer id,
                                  @Valid @ModelAttribute("address") Address address,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Address");
            return "secured/address/edit";
        }
        address.setAddressId(id);
        Address updated = addressService.updateAddress(address);
        if (updated == null) {
            ra.addFlashAttribute("errorMessage", "Unable to update address ID: " + id);
            return "redirect:/secured/address/list";
        }
        ra.addFlashAttribute("successMessage", "Address has been updated.");
        return "redirect:/secured/address/list";
    }

    @GetMapping("/secured/address/view/{id}")
    public String viewAddress(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            ra.addFlashAttribute("errorMessage", "Address not found with ID: " + id);
            return "redirect:/secured/address/list";
        }
        model.addAttribute("address", address);
        model.addAttribute("pageTitle", "Address Details");
        return "secured/address/view";
    }

    /** Delete address (from UI) */
    @PostMapping("/secured/address/delete/{id}")
    public String deleteAddressUi(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            boolean deleted = addressService.deleteAddressById(id);
            if (deleted) {
                ra.addFlashAttribute("successMessage", "Address has been successfully deleted.");
            } else {
                ra.addFlashAttribute("errorMessage", "Address not found with ID: " + id);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error deleting address: " + e.getMessage());
        }
        return "redirect:/secured/address/list";
    }

    // ===================== REST API endpoints =====================

    @ResponseBody
    @GetMapping("/dentalsugery/api/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getAllAddressesSortedByCity() {
        List<AddressResponseDTO> addresses = addressService.getAllAddressesSortedByCity();
        return ResponseEntity.ok(addresses);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/addresses/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Integer id) {
        Address address = addressService.getAddressById(id);
        if (address != null) {
            return ResponseEntity.ok(mapToDTO(address));
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @PostMapping("/dentalsugery/api/addresses")
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody Address address) {
        Address createdAddress = addressService.addNewAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdAddress));
    }

    @ResponseBody
    @PutMapping("/dentalsugery/api/addresses/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Integer id, @RequestBody Address address) {
        address.setAddressId(id);
        Address updatedAddress = addressService.updateAddress(address);
        if (updatedAddress != null) {
            return ResponseEntity.ok(mapToDTO(updatedAddress));
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @DeleteMapping("/dentalsugery/api/addresses/{id}")
    public ResponseEntity<DeleteResponseDTO> deleteAddress(@PathVariable Integer id) {
        boolean deleted = addressService.deleteAddressById(id);
        if (deleted) {
            DeleteResponseDTO response = new DeleteResponseDTO(true,
                    "Address with ID " + id + " has been successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            DeleteResponseDTO response = new DeleteResponseDTO(false, "Address with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/addresses/with-patients")
    public ResponseEntity<List<AddressWithPatientsResponseDTO>> getAllAddressesWithPatientsSortedByCity() {
        List<AddressWithPatientsResponseDTO> addresses = addressService.getAllAddressesWithPatientsSortedByCity();
        return ResponseEntity.ok(addresses);
    }

    private AddressResponseDTO mapToDTO(Address address) {
        return new AddressResponseDTO(
                address.getAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipcode());
    }
}
