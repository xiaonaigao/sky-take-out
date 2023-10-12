package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 套餐管理
 */
@Service
public class SetmealServiceImpl implements SetmealService {
	@Autowired
	private SetmealMapper setmealMapper;
	@Autowired
	private SetmealDishMapper setmealDishMapper;
	@Autowired
	private DishMapper dishMapper;

	/**
	 * 新增套餐
	 *
	 * @param setmealDTO
	 */
	@Override
	@Transactional
	public void save(SetmealDTO setmealDTO) {
		// 1.拆分数据	,添加套餐表Setmeal
		Setmeal setmeal = new Setmeal(); //套餐主页的
		BeanUtils.copyProperties(setmealDTO, setmeal);
		setmealMapper.insert(setmeal);

		// 2.添加套餐的相关菜品，获取此套餐表的setmealId
		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
		Long setmealId = setmeal.getId();
		if (setmealDishes != null && setmealDishes.size() > 0) {
			setmealDishes.forEach(setmealDish -> {
				setmealDish.setSetmealId(setmealId);
			});
		}
		setmealDishMapper.insertBatch(setmealDishes);
	}

	/**
	 * 分页查询
	 *
	 * @param setmealPageQueryDTO
	 * @return
	 */
	@Override
	public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
		PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
		Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 删除套餐
	 *
	 * @param ids
	 */
	@Override
	@Transactional
	public void deleteBatch(List<Long> ids) {
		// 0.启售的套餐不能删除
		for (Long id : ids) {
			Setmeal setmeal = setmealMapper.getById(id);
			if (setmeal.getStatus() == StatusConstant.ENABLE) {
				throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
			}
		}
		// 1.删除套餐的关联的setmeal_dish
		setmealDishMapper.deleteBatch(ids);
		// 2.删除套餐setmeal
		setmealMapper.deleteBatch(ids);
	}
	/**
	 * 根据id查询
	 */
	@Override
	public SetmealVO getId(Long id) {
		SetmealVO setmealVO = new SetmealVO();
		//1.查询套餐的信息
		Setmeal setmeal = setmealMapper.getById(id);
		BeanUtils.copyProperties(setmeal,setmealVO);
		//2.查询套餐的菜品
		List<SetmealDish> setmealDishList = setmealDishMapper.getSetmealDishBySetmealId(id);
		setmealVO.setSetmealDishes(setmealDishList);
		return setmealVO;
	}
	/**
	 * 修改套餐
	 */
	@Override
	public void edit(SetmealDTO setmealDTO) {
		// 修改套餐：套餐的信息，菜品的信息
		// 1.套餐的信息 创建setmeal对象 通过拷贝往前传 在语句上写更新
		Setmeal setmeal = new Setmeal();
		BeanUtils.copyProperties(setmealDTO,setmeal);
		setmealMapper.update(setmeal);
		// 2.菜品的信息 首先全部删除 然后重新添加（设置sermealId）
		Long setmealId = setmeal.getId();
		setmealDishMapper.delete(setmealId);//删除
		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
		if (setmealDishes != null && setmealDishes.size() > 0) {
			for (SetmealDish setmealDish:setmealDishes){
				setmealDish.setSetmealId(setmealId);//设置套餐id
			}
		}

		setmealDishMapper.insertBatch(setmealDishes);//插入套餐的菜单

	}
	/**
	 * 起售 停售
	 */
	@Override
	public void status(Integer status, Long id) {
		// 1.起售时判断菜品有无停售
		if (status == StatusConstant.ENABLE){
			// 根据套餐id获取所有的菜品
			List<SetmealDish> setmealDishs = setmealDishMapper.getSetmealDishBySetmealId(id);
			// 根据菜品id去查询有无停售的
			for (SetmealDish setmealDish : setmealDishs) {
				Dish dish = dishMapper.getById(setmealDish.getDishId()); //查找到菜品的状态
				if (dish.getStatus() == StatusConstant.DISABLE){ // 菜品有未启用的
					throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
				}
			}
		}

		// 2.起售停售
		Setmeal setmeal = new Setmeal();
		setmeal.setStatus(status);
		setmeal.setId(id);
		setmealMapper.update(setmeal);
	}

	@Override
	public List<Setmeal> list(Setmeal setmeal) {
		List<Setmeal> list = setmealMapper.list(setmeal);
		return list;
	}

	@Override
	public List<DishItemVO> getDishItemById(Long id) {
		return setmealMapper.getDishItemBySetmealId(id);
	}
}
