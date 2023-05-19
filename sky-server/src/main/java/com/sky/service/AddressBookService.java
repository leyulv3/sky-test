package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void add(AddressBook address);

    List<AddressBook> list();

    AddressBook defaultAddress();

    void updateDefault(AddressBook address);

    AddressBook getById(Long id);

    void updateAddress(AddressBook addressBook);

    void deleteById(Long id);
}
