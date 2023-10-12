package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wang
 * @version 1.0
 */
@Service
public class AddressBookServiceImpl implements AddressBookService {
	@Autowired
	private AddressBookMapper addressBookMapper;

	/**
	 * 新增地址
	 *
	 * @param addressBook
	 */
	@Override
	public void save(AddressBook addressBook) {
		// 数据封装
		addressBook.setUserId(BaseContext.getCurrentId());
		addressBook.setIsDefault(0);
		// 调用mapper
		addressBookMapper.insert(addressBook);

	}

	/**
	 * 查询登录用户所有地址
	 *
	 * @return
	 */
	@Override
	public List<AddressBook> list() {
		AddressBook addressBook = new AddressBook();
		addressBook.setUserId(BaseContext.getCurrentId());
		return addressBookMapper.list(addressBook);
	}

	/**
	 * 默认地址
	 *
	 * @return
	 */
	@Override
	public AddressBook defaultAddress() {
		// 封装参数，userId,setIsDefault设为1
		AddressBook addressBook = new AddressBook();
		addressBook.setUserId(BaseContext.getCurrentId());
		addressBook.setIsDefault(1);
		// 查询
		List<AddressBook> list = addressBookMapper.list(addressBook);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 根据id查询地址
	 */
	@Override
	public AddressBook getById(Long id) {
		return addressBookMapper.getById(id);
	}
	/**
	 * 根据id修改
	 */
	@Override
	public void edit(AddressBook addressBook) {
		addressBookMapper.update(addressBook);
	}

	/**
	 * 根据id删除地址
	 * @param id
	 */
	@Override
	public void deleteById(Long id) {
		addressBookMapper.deleteById(id);
	}
	/**
	 * 设置默认地址
	 * @param addressBook
	 * @return
	 */
	@Override
	public void setDefaultAddress(AddressBook addressBook) {
		// 1.把当前用户所有的状态设为0 条件是 userId
		addressBook.setIsDefault(0);
		addressBook.setUserId(BaseContext.getCurrentId());
		addressBookMapper.updateByUserId(addressBook);

		// 2.设置当前地址id的默认为1，条件是id
		addressBook.setIsDefault(1);
		addressBookMapper.update(addressBook);
	}
}
