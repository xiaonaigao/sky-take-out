package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author wang
 * @version 1.0
 */
@Mapper
public interface ShoppingCartMapper {
	/**
	 * 查询套餐【菜品id,套餐id】
	 * @param shoppingCart
	 * @return
	 */
	List<ShoppingCart> getList(ShoppingCart shoppingCart);

	/**
	 * 更新数量
	 * @param shoppingCart
	 */
	@Update("update shopping_cart set number = #{number} where id=#{id}")
	void updateNumberById(ShoppingCart shoppingCart);

	/**
	 * 插入套餐
	 * @param shoppingCart
	 */
	@Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) values" +
			" (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
	void insert(ShoppingCart shoppingCart);

	/**
	 * 清空购物车
	 * @param
	 */
	@Delete("delete from shopping_cart where user_id = #{userId}")
	void deleteById(Long userId);

	/**
	 * 清购物车的数量为0商品
	 * @param shoppingCart
	 */
	void deleteByDishIdOrBySetMealId(ShoppingCart shoppingCart);

	/**
	 * 批量添加购物车
	 * @param shoppingCartList
	 */
	void insertBatch(List<ShoppingCart> shoppingCartList);
}
