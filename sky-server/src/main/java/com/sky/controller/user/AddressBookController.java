package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址实现
 */
@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "地址管理")
public class AddressBookController {
	@Autowired
	private AddressBookService addressBookService;
	/**
	 * 新增地址
	 * @param addressBook
	 * @return
	 */
	@PostMapping
	@ApiOperation("新增地址")
	public Result add(@RequestBody AddressBook addressBook){
		log.info("新增地址{}",addressBook);
		addressBookService.save(addressBook);
		return Result.success();
	}

	/**
	 * 查询登录用户所有地址
	 * @return
	 */
	@GetMapping("/list")
	@ApiOperation("当前用户所有地址")
	public Result<List<AddressBook>> list(){
		log.info("查询登录用户所有地址");
		List<AddressBook> list =addressBookService.list();
		return Result.success(list);
	}
	/**
	 * 查询默认地址
	 */
	@GetMapping("/default")
	@ApiOperation("查询默认地址")
	public Result<AddressBook> defaultAddress(){
		log.info("查询默认地址");
		AddressBook defaultAddress =addressBookService.defaultAddress();
		return Result.success(defaultAddress);
	}

	/**
	 * 根据id查询地址
	 */
	@GetMapping("/{id}")
	@ApiOperation("根据id查询地址")
	public Result<AddressBook> getById(@PathVariable Long id){
		log.info("根据id查询地址{}",id);
		AddressBook addressBook=addressBookService.getById(id);
		return Result.success(addressBook);
	}

	/**
	 * 根据id修改
	 */
	@PutMapping
	@ApiOperation("根据id修改")
	public Result edit(@RequestBody AddressBook addressBook){
		log.info("修改后的地址{}",addressBook);
		addressBookService.edit(addressBook);
		return Result.success();
	}

	/**
	 * 根据id删除地址
	 */
	@DeleteMapping
	@ApiOperation("根据id删除地址")
	public Result delete(Long id){
		log.info("删除的id是{}",id);
		addressBookService.deleteById(id);
		return Result.success();
	}

	/**
	 * 设置默认地址
	 * @param addressBook
	 * @return
	 */
	@PutMapping("/default")
	@ApiOperation("设置默认地址")
	public Result setDefaultAddress(@RequestBody AddressBook addressBook){
		log.info("默认地址id是{}",addressBook);
		addressBookService.setDefaultAddress(addressBook);
		return Result.success();
	}
}