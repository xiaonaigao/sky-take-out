package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车实现
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
	@Autowired
	private ShoppingCartMapper shoppingCartMapper;
	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private SetmealMapper setmealMapper;

	/**
	 * 添加购物车
	 *
	 * @param shoppingCartDTO
	 */
	@Override
	public void add(ShoppingCartDTO shoppingCartDTO) {
		// 1.判断是否存在，如果存在数量加1
		ShoppingCart shoppingCart = new ShoppingCart();
		BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
		shoppingCart.setUserId(BaseContext.getCurrentId());
		// 判断套餐是否存在
		List<ShoppingCart> cartList = shoppingCartMapper.getList(shoppingCart);
		if (cartList != null && cartList.size() == 1) {
			// 套餐存在，数量加1.
			shoppingCart = cartList.get(0);
			Integer number = shoppingCart.getNumber() + 1;
			shoppingCart.setNumber(number);
			// 更新数量
			shoppingCartMapper.updateNumberById(shoppingCart);
			return;

		}
		// 不存在 插入数据
		// 2.判断是菜品 还是 套餐
		Long dishId = shoppingCart.getDishId();
		if (dishId != null) { // 添加是菜品
			Dish dish = dishMapper.getById(dishId);
			shoppingCart.setAmount(dish.getPrice());

			shoppingCart.setName(dish.getName());
			shoppingCart.setImage(dish.getImage());

		} else { // 添加的是套餐
			Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
			shoppingCart.setAmount(setmeal.getPrice());
			shoppingCart.setName(setmeal.getName());
			shoppingCart.setImage(setmeal.getImage());
		}
		shoppingCart.setNumber(1);
		shoppingCart.setCreateTime(LocalDateTime.now());
		// 插入购物车表
		shoppingCartMapper.insert(shoppingCart);

	}

	/**
	 * 购物车的明细
	 * @return
	 */
	@Override
	public List<ShoppingCart> list() {
		ShoppingCart shoppingCart = new ShoppingCart();
		shoppingCart.setUserId(BaseContext.getCurrentId());
		return shoppingCartMapper.getList(shoppingCart);
	}

	/**
	 * 清空购物车
	 */

	@Override
	public void clean() {
		shoppingCartMapper.deleteById(BaseContext.getCurrentId());
	}

	/**
	 * 购物车数量减1
	 * @param shoppingCartDTO
	 */
	@Override
	public void sub(ShoppingCartDTO shoppingCartDTO) {
		ShoppingCart shoppingCart = new ShoppingCart();
		BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
		shoppingCart.setUserId(BaseContext.getCurrentId());
		// 查询套餐
		List<ShoppingCart> cartList = shoppingCartMapper.getList(shoppingCart);
		// 获取套餐
		shoppingCart = cartList.get(0);
		Integer number = shoppingCart.getNumber();
		if (number > 1){
			shoppingCart.setNumber(number-1);
			shoppingCartMapper.updateNumberById(shoppingCart);
		}
		if (number == 1){
			shoppingCartMapper.deleteByDishIdOrBySetMealId(shoppingCart);
		}
	}
}
