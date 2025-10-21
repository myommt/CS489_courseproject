package cs489.miu.dentalsurgeryapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cs489.miu.dentalsurgeryapp.model.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    
    // Method to find all addresses sorted by city in ascending order
    List<Address> findAllByOrderByCityAsc();
    
    // Method to find existing address by street, city, state, and zipcode
    Optional<Address> findByStreetAndCityAndStateAndZipcode(String street, String city, String state, String zipcode);
    
    // Method to find all addresses with their patients, sorted by city
    @Query("SELECT DISTINCT a FROM Address a LEFT JOIN FETCH Patient p WHERE p.address = a ORDER BY a.city ASC")
    List<Address> findAllAddressesWithPatientsSortedByCity();

}
