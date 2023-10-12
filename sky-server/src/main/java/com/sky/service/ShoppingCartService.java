package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * 购物车实现
 */
public interface ShoppingCartService {
	/**
	 * 添加购物车
	 * @param shoppingCartDTO
	 */
	void add(ShoppingCartDTO shoppingCartDTO);

	/**
	 * 查看购物车
	 * @return
	 */
	List<ShoppingCart> list();

	/**
	 * 清空购物车
	 */
	void clean();

	/**
	 * 购物车数量减1
	 * @param shoppingCartDTO
	 */
	void sub(ShoppingCartDTO shoppingCartDTO);
}
