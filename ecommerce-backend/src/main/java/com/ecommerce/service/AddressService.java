package com.ecommerce.service;

import com.ecommerce.entity.Address;

import java.util.List;

public interface AddressService {

    Address addAddress(Address address);

    List<Address> getMyAddresses();

    Address updateAddress(Long id, Address updatedAddress);

    void deleteAddress(Long id);
}