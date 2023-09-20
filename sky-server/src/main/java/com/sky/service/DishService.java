package com.sky.service;

import com.sky.dto.DishDTO;

/**
 * 菜品管理
 */
public interface DishService {
	/**
	 * 新增菜品
	 */
	public void saveWithFlavor(DishDTO dishDTO);
}
