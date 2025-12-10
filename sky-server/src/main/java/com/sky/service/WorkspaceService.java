package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

/**
 * 工作台接口
 */
public interface WorkspaceService {
	/**
	 * 今日数据
	 * @return
	 */
	BusinessDataVO businessData();
	BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);
	/**
	 * 查询订单管理数据
	 * @return
	 */
	OrderOverViewVO overviewOrders();
	/**
	 * 查询菜品总览
	 * @return
	 */
	DishOverViewVO getDishOverView();
	/**
	 * 查询套餐总览
	 * @return
	 */
	SetmealOverViewVO getSetmealOverView();


}
