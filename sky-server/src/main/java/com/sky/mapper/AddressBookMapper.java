package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    @Insert("insert into address_book (user_id, consignee, sex, " +
            "phone, province_code, province_name, city_code, city_name, " +
            "district_code, district_name, detail, label,is_default)" +
            "VALUES (#{userId},#{consignee}, #{sex}, #{phone}," +
            "#{provinceCode}, #{provinceName}, #{cityCode},#{ cityName}, " +
            "#{districtCode}, #{districtName}, #{detail}, #{label},#{isDefault})")
    void add(AddressBook address);
    @Select("select * from address_book where user_id = #{currentId}")
    List<AddressBook> selectByUserId(Long currentId);
    @Select("select * from address_book where user_id = #{currentId} and is_default = 1")
    AddressBook selectDefault(Long currentId);
    @Update("update address_book set is_default=1 where user_id=#{userId} and id=#{id}")
    void updateDefault(AddressBook address);
    @Update("update address_book set is_default=0 where user_id=#{userId}")
    void updateNoDefault(AddressBook address);
    @Select("select * from address_book where id=#{id}")
    AddressBook selectById(Long id);
    @Delete("delete from address_book where id=#{id}")
    void deleteById(Long id);

    void updateAddress(AddressBook addressBook);
}
