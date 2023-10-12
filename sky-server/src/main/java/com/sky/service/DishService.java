package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * 菜品管理
 */
public interface DishService {
	/**
	 * 新增菜品
	 */
	public void saveWithFlavor(DishDTO dishDTO);

	/**
	 * 分页查询
	 * @param dishPageQueryDTO
	 * @return
	 */
	PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
	/**
	 * 删除菜品
	 */
	void deleteBatch(List<Long> ids);

	/**
	 * 查询菜品
	 * @param id
	 * @return
	 */
	DishVO getByIdWithFlavor(Long id);

	/**
	 * 修改菜品和口味
	 * @param dishDTO
	 */
	void updateWithFlavor(DishDTO dishDTO);

	/**
	 * 启用禁用
	 * @param status
	 * @param id
	 */
	void startOrStop(Integer status, Long id);
	/**
	 * 根据分类id查询菜品
	 */
	List<Dish> getBycategoryIdForDish(Integer categoryId);

	List<DishVO> listWithFlavor(Dish dish);
}
