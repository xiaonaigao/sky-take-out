package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * 店铺设置
 */
@Service
public class ShopServiceImpl implements ShopService {
	/**
	 * 设置店铺状态
	 *
	 * @param status
	 */
	@Autowired
	private RedisTemplate redisTemplate;

	public static final String KET = "SHOP_STATUS";

	@Override
	public void setShopStatus(Integer status) {
		redisTemplate.opsForValue().set(KET, status);
	}

	/**
	 * 获取状态
	 *
	 * @return
	 */
	@Override
	public Integer getShopStatus() {
		return (Integer) redisTemplate.opsForValue().get(KET);
	}
}
