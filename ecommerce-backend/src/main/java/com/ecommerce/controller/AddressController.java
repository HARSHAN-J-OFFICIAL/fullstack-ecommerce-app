package com.ecommerce.controller;

import com.ecommerce.entity.Address;
import com.ecommerce.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Addresses", description = "Endpoints for managing user delivery addresses")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Add new delivery address", description = "Saves a new shipping address for the logged-in user.")
    @PostMapping
    public Address addAddress(@RequestBody Address address) {
        return addressService.addAddress(address);
    }

    @Operation(summary = "Get user delivery addresses", description = "Retrieves all saved shipping addresses for the logged-in user.")
    @GetMapping
    public List<Address> getMyAddresses() {
        return addressService.getMyAddresses();
    }

    @Operation(summary = "Update address", description = "Updates an existing shipping address by address ID.")
    @PutMapping("/{id}")
    public Address updateAddress(
            @PathVariable Long id,
            @RequestBody Address address
    ) {
        return addressService.updateAddress(id, address);
    }

    @Operation(summary = "Delete address", description = "Deletes a shipping address by address ID.")
    @DeleteMapping("/{id}")
    public String deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return "Address deleted successfully";
    }
}