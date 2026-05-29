package com.ecommerce.service.impl;

import com.ecommerce.entity.Address;
import com.ecommerce.entity.User;

import com.ecommerce.exception.ResourceNotFoundException;

import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;

import com.ecommerce.service.AddressService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    // OWNERSHIP VALIDATION
    private Address getAddressOwnedByUser(Long id) {

        User user = getCurrentUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found"
                        ));

        if (!address.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this address"
            );
        }

        return address;
    }

    @Override
    public Address addAddress(Address address) {

        User user = getCurrentUser();

        address.setUser(user);

        return addressRepository.save(address);
    }

    @Override
    public List<Address> getMyAddresses() {

        User user = getCurrentUser();

        return addressRepository.findByUser(user);
    }

    @Override
    public Address updateAddress(
            Long id,
            Address updatedAddress
    ) {

        Address address = getAddressOwnedByUser(id);

        address.setFullName(updatedAddress.getFullName());

        address.setPhoneNumber(updatedAddress.getPhoneNumber());

        address.setStreet(updatedAddress.getStreet());

        address.setCity(updatedAddress.getCity());

        address.setState(updatedAddress.getState());

        address.setCountry(updatedAddress.getCountry());

        address.setPincode(updatedAddress.getPincode());

        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long id) {

        Address address = getAddressOwnedByUser(id);

        addressRepository.delete(address);
    }
}