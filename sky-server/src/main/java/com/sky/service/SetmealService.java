package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * 套餐管理
 */
public interface SetmealService {
	/**
	 * 新增套餐
	 * @param setmealDTO
	 */
	public void save(SetmealDTO setmealDTO);

	/**
	 * 分页查询
	 * @param setmealPageQueryDTO
	 * @return
	 */
	PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

	/**
	 * 删除套餐
	 * @param ids
	 */
	void deleteBatch(List<Long> ids);
	/**
	 * 根据id查询
	 */
	SetmealVO getId(Long id);
	/**
	 * 修改套餐
	 */
	void edit(SetmealDTO setmealDTO);
	/**
	 * 起售 停售
	 */
	void status(Integer status, Long id);

	List<Setmeal> list(Setmeal setmeal);

	List<DishItemVO> getDishItemById(Long id);
}
