package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wang
 * @version 1.0
 */
@Mapper
public interface SetmealDishMapper {
	/**
	 * 菜品id-查询套餐id
	 * @param dishIds
	 * @return
	 */
	List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

	/**
	 * 套餐关联的菜品
	 * @param setmealDishes
	 */
	void insertBatch(List<SetmealDish> setmealDishes);

	/**
	 * 删除套餐的菜单
	 * @param ids
	 */
	void deleteBatch(List<Long> ids);

	/**
	 * 根据id查询套餐的信息
	 * @param id
	 * @return
	 */
	@Select("select *  from setmeal_dish where setmeal_id = #{id} ")
	List<SetmealDish> getSetmealDishBySetmealId(Long id);

	/**
	 * 根据id删除
	 * @param setmealId
	 */
	@Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
	void delete(Long setmealId);
}
