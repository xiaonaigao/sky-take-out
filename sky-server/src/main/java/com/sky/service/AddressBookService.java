package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

/**
 * @author wang
 * @version 1.0
 */
public interface AddressBookService {
	/**
	 * 新增地址
	 * @param addressBook
	 */
	void save(AddressBook addressBook);

	/**
	 * 查询登录用户所有地址
	 * @return
	 */
	List<AddressBook> list();
	/**
	 * 查询默认地址
	 * @return
	 */
	AddressBook defaultAddress();
	/**
	 * 根据id查询地址
	 */
	AddressBook getById(Long id);
	/**
	 * 根据id修改
	 */
	void edit(AddressBook addressBook);
	/**
	 * 根据id删除地址
	 */
	void deleteById(Long id);
	/**
	 * 设置默认地址
	 * @param addressBook
	 * @return
	 */
	void setDefaultAddress(AddressBook addressBook);
}
