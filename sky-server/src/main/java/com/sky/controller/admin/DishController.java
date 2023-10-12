package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {
	@Autowired
	private DishService dishService;
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 清理redis缓存
	 */
	private void cleanCache(String pattern){
		// 获取key集合
		Set keys = redisTemplate.keys(pattern);
		// 删除key集合
		redisTemplate.delete(keys);
	}

	/**
	 * 上传菜品
	 */
	@PostMapping
	@ApiOperation("新增菜品")
	public Result save(@RequestBody DishDTO dishDTO) {
		log.info("新增菜品{}", dishDTO);
		// 调用增加的service
		dishService.saveWithFlavor(dishDTO);
		// 获取分类的id
		Long categoryId = dishDTO.getCategoryId();
		String key = "dish_"+categoryId;
		// 清理该缓存
		cleanCache(key);
		return Result.success();
	}

	/**
	 * 菜品分页查询
	 */
	@GetMapping("/page")
	@ApiOperation("菜品分页查询")
	public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
		log.info("菜品分页查询{}", dishPageQueryDTO);
		PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
		return Result.success(pageResult);
	}

	/**
	 * 删除菜品
	 */
	@DeleteMapping
	@ApiOperation("删除菜品")
	public Result delete(@RequestParam List<Long> ids){
		log.info("菜品批量删除{}",ids);
		// 调用业务层
		dishService.deleteBatch(ids);
		cleanCache("dish_*");
		return Result.success();
	}

	/**
	 * 根据id查询
	 */
	@GetMapping("/{id}")
	@ApiOperation("根据id查询菜品")
	public Result<DishVO> getById(@PathVariable Long id){
		log.info("查询菜品{}",id);
		DishVO dishVO = dishService.getByIdWithFlavor(id);
		return Result.success(dishVO);
	}

	/**
	 * 修改菜品
	 */
	@PutMapping
	@ApiOperation("修改菜品信息")
	public Result update(@RequestBody DishDTO dishDTO){
		log.info("查询菜品{}",dishDTO);
		dishService.updateWithFlavor(dishDTO);
		cleanCache("dish_*");
		return Result.success();
	}

	/**
	 * 启用，禁用
	 */
	@PostMapping("/status/{status}")
	@ApiOperation("启用禁用")
	public Result startOrStop(@PathVariable Integer status,Long id){
		log.info("启用禁用{},{}",status,id);
		dishService.startOrStop(status,id);
		cleanCache("dish_*");
		return Result.success();
	}

	/**
	 * 根据分类id查询菜品
	 */
	@GetMapping("/list")
	@ApiOperation("根据分类id查询菜品")
	public Result<List<Dish>> list(Integer categoryId){
		log.info("分类id是{}",categoryId);
		List<Dish> dishList =dishService.getBycategoryIdForDish(categoryId);
		return Result.success(dishList);
	}

}
