package com.sky.service;

/**
 * @author wang
 * @version 1.0
 */
public interface ShopService {
	/**
	 * 设置状态
	 */
	void setShopStatus(Integer status);

	/**
	 * 获取状态
	 * @return
	 */
	Integer getShopStatus();
}
