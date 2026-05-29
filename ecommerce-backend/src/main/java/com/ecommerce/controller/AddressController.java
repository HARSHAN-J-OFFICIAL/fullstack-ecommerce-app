package com.ecommerce.controller;

import com.ecommerce.entity.Address;

import com.ecommerce.service.AddressService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // Add new address
    @PostMapping
    public Address addAddress(
            @RequestBody Address address
    ) {

        return addressService.addAddress(address);
    }

    // Get logged-in user's addresses
    @GetMapping
    public List<Address> getMyAddresses() {

        return addressService.getMyAddresses();
    }

    // Update address
    @PutMapping("/{id}")
    public Address updateAddress(
            @PathVariable Long id,
            @RequestBody Address address
    ) {

        return addressService.updateAddress(id, address);
    }

    // Delete address
    @DeleteMapping("/{id}")
    public String deleteAddress(
            @PathVariable Long id
    ) {

        addressService.deleteAddress(id);

        return "Address deleted successfully";
    }
}