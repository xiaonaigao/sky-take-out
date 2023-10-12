package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wang
 * @version 1.0
 */
@Mapper
public interface DishFlavorMapper {
	/**
	 * 批量插入口味
	 *
	 * @param flavors
	 */
	void insertBatch(List<DishFlavor> flavors);

	/**
	 * 删除口味
	 */
	@Delete("delete from dish_flavor where dish_id = #{dishId}")
	void deleteById(Long dishId);

	/**
	 * 批量删除口味
	 */
	void deleteByDishIds(List<Long> dishIds);

	/**
	 * 获取口味
	 *
	 * @param dishId
	 * @return
	 */
	@Select("select * from dish_flavor where dish_id = #{dishId}")
	List<DishFlavor> getByDishId(Long dishId);
}
