package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;

import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    //新增地址
    @PostMapping
    public Result add(@RequestBody AddressBook address) {
        addressBookService.add(address);
        return Result.success();
    }

    //- 查询登录用户所有地址
    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        return Result.success(addressBookService.list());
    }

    //- 查询默认地址
    @GetMapping("/default")
    public Result<AddressBook> defaultAddress() {
        return Result.success(addressBookService.defaultAddress());
    }

    //- 设置默认地址
    @PutMapping("/default")
    public Result updateDefault(@RequestBody AddressBook address) {
        addressBookService.updateDefault(address);
        return Result.success();
    }

    //- 根据id查询地址
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(addressBookService.getById(id));
    }

    //- 根据id修改地址
    @PutMapping
    public Result updateAddress(@RequestBody AddressBook addressBook) {
        addressBookService.updateAddress(addressBook);
        return Result.success();
    }

    //- 根据id删除地址
    @DeleteMapping()
    public Result deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

}
