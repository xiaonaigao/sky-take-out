package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜品管理
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {
	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private DishFlavorMapper dishFlavorMapper;

	/**
	 * 新增菜品
	 */
	@Override
	@Transactional
	public void saveWithFlavor(DishDTO dishDTO) {
		// 1.菜品表插入数据 1条
		//封装参数
		Dish dish = new Dish();
		BeanUtils.copyProperties(dishDTO, dish);
		// 插入菜品后返回菜品的主键值
		dishMapper.insert(dish);
		Long dishId = dish.getId();

		// 2.口味表关联菜品 n条
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && flavors.size() > 0){
			// 遍历口味设置菜品id
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishId);
			});
			// 口味批量插入
			dishFlavorMapper.insertBatch(flavors);
		}


	}
}
