package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地址管理
 */
@Mapper
public interface AddressBookMapper {
	/**
	 * 新增地址
	 * @param addressBook
	 */
	@Insert("insert into address_book(user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) values " +
			"(#{userId},#{consignee},#{sex},#{phone},#{provinceCode},#{provinceName},#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label},#{isDefault})")
	void insert(AddressBook addressBook);

	/**
	 * 用户所有的地址
	 * @param addressBook
	 * @return
	 */
	List<AddressBook> list(AddressBook addressBook);
	/**
	 * 根据id查询地址
	 */
	@Select("select * from address_book where id = #{id}")
	AddressBook getById(Long id);
	/**
	 * 根据id修改
	 */
	void update(AddressBook addressBook);

	/**
	 * 根据id删除地址
	 * @param id
	 */
	@Delete("delete from address_book where id =#{id}")
	void deleteById(Long id);
	/**
	 * 设置默认地址:全为0
	 * @param addressBook
	 * @return
	 */
	@Update("update address_book set is_default = #{isDefault} where user_id=#{userId}")
	void updateByUserId(AddressBook addressBook);
}
