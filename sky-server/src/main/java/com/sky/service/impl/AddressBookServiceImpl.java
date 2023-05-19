package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressMapper;

    @Override
    public void add(AddressBook address) {
        //AddressBook
        // (id=null, userId=null, consignee=11, phone=15678987654,
        // sex=0, provinceCode=11, provinceName=北京市, cityCode=1101,
        // cityName=市辖区, districtCode=110102, districtName=西城区,
        // detail=11, label=1, isDefault=null)
        address.setUserId(BaseContext.getCurrentId());
        address.setIsDefault(0);
        addressMapper.add(address);
    }

    @Override
    public List<AddressBook> list() {
        return addressMapper.selectByUserId(BaseContext.getCurrentId());
    }

    @Override
    public AddressBook defaultAddress() {
        return addressMapper.selectDefault(BaseContext.getCurrentId());
    }

    @Override
    public void updateDefault(AddressBook address) {
        address.setUserId(BaseContext.getCurrentId());
        //将所有的地址都设置为非默认
        addressMapper.updateNoDefault(address);
        //将当前地址设置为默认
        addressMapper.updateDefault(address);
    }

    @Override
    public AddressBook getById(Long id) {
        return addressMapper.selectById(id);
    }

    @Override
    public void updateAddress(AddressBook addressBook) {
        addressMapper.updateAddress(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        addressMapper.deleteById(id);
    }
}
