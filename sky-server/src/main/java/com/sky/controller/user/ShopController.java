package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 店铺设置
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "店铺营业状态")
public class ShopController {

	@Autowired
	private ShopService shopService;
	/**
	 * 	查询营业状态
	 */
	@GetMapping("/status")
	@ApiOperation("查询营业状态")
	public Result<Integer> getShop(){
		Integer status =shopService.getShopStatus();
		return Result.success(status);
	}
}
