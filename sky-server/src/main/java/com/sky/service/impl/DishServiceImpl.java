package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
	@Autowired
	private SetmealDishMapper setmealDishMapper;

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
		if (flavors != null && flavors.size() > 0) {
			// 遍历口味设置菜品id
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishId);
			});
			// 口味批量插入
			dishFlavorMapper.insertBatch(flavors);
		}


	}

	/**
	 * 分页查询
	 *
	 * @param dishPageQueryDTO
	 * @return
	 */
	@Override
	public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
		PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());//开始分页
		Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 删除菜品
	 */

	@Override
	@Transactional
	public void deleteBatch(List<Long> ids) {
		// 起售不能删除，关联套餐不删除，删除菜品对应的口味
		// 1.判断菜品是否起售
		for (Long id : ids) {
			Dish dish = dishMapper.getById(id);
			if (dish.getStatus() == StatusConstant.ENABLE) {
				// 起售不能删除
				throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
			}
		}
		// 2.判断菜品是否套餐关联
		List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
		if (setmealIds != null && setmealIds.size() > 0) {
			throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
		}
		// 3.批量删除菜品
		// for (Long id : ids) {
		// 	dishMapper.deleteById(id);
		// 	// 4.删除对应口味表
		// 	dishFlavorMapper.deleteById(id);
		// }
		dishMapper.deleteByIds(ids);

		// 4.批量删除口味
		dishFlavorMapper.deleteByDishIds(ids);


	}

	/**
	 * 查询菜品和口味
	 *
	 * @param id
	 * @return
	 */
	@Override
	public DishVO getByIdWithFlavor(Long id) {
		// 根据id查询菜品
		Dish dish = dishMapper.getById(id);
		//菜品id查询口味
		List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
		// 封装
		DishVO dishVO = new DishVO();
		BeanUtils.copyProperties(dish, dishVO);
		dishVO.setFlavors(dishFlavors);
		return dishVO;
	}

	/**
	 * 修改菜品和口味
	 *
	 * @param dishDTO
	 */
	@Override
	@Transactional
	public void updateWithFlavor(DishDTO dishDTO) {
		// 1.修改菜品
		Dish dish = new Dish();
		BeanUtils.copyProperties(dishDTO, dish);
		dishMapper.update(dish);

		// 2.删除口味。然后插入新数据
		dishFlavorMapper.deleteById(dish.getId());
		// 插入新数据
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && flavors.size() > 0) {
			for (DishFlavor flavor : flavors) {
				flavor.setDishId(dish.getId());
			}
		}
		dishFlavorMapper.insertBatch(flavors);


	}

	/**
	 * 启用禁用
	 *
	 * @param status
	 * @param id
	 */
	@Override
	public void startOrStop(Integer status, Long id) {
		// 封装参数
		Dish dish = new Dish();
		dish.setStatus(status);
		dish.setId(id);
		//调用mapper
		dishMapper.update(dish);
	}

	/**
	 * 根据分类id查询菜品
	 */
	@Override
	public List<Dish> getBycategoryIdForDish(Integer categoryId) {
		List<Dish> dishList = dishMapper.getByCategoryId(categoryId);
		return dishList;
	}

	@Override
	public List<DishVO> listWithFlavor(Dish dish) {
		List<Dish> dishList = dishMapper.list(dish);

		List<DishVO> dishVOList = new ArrayList<>();

		for (Dish d : dishList) {
			DishVO dishVO = new DishVO();
			BeanUtils.copyProperties(d,dishVO);

			//根据菜品id查询对应的口味
			List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

			dishVO.setFlavors(flavors);
			dishVOList.add(dishVO);
		}

		return dishVOList;
	}


}
