package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 工作台实现类
 */
@Service
public class WorkspaceServiceImpl implements WorkspaceService {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private SetmealMapper setmealMapper;

	/**
	 * 今日数据
	 *
	 * @return
	 */
	@Override
	public BusinessDataVO businessData() {
		// 获取今日的时间
		LocalDate dateTime = LocalDate.now();
		LocalDateTime begin = LocalDateTime.of(dateTime, LocalTime.MIN);
		LocalDateTime end = LocalDateTime.of(dateTime, LocalTime.MAX);
		Map map = new HashMap();
		map.put("begin", begin);
		map.put("end", end);
		// 1.新增用户数
		Integer newUsers = userMapper.countByMap(map);
		// 2.订单完成率
		Integer sumOrder = orderMapper.getCountOrderByStatus(map);//当天全部订单
		map.put("status", Orders.COMPLETED);
		Integer validOrderCount = orderMapper.getCountOrderByStatus(map);    // 5.有效订单数
		Double orderCompletionRate = 0.0;//订单完成率
		if (sumOrder != 0) {
			orderCompletionRate = validOrderCount.doubleValue() / sumOrder;
		}
		// 3.营业额
		Double turnover = orderMapper.sumByMap(map);
		turnover = turnover == null ? 0.0 : turnover;
		// 4.平均客单价
		Double unitPrice = 0.0;
		if (validOrderCount != 0) {
			unitPrice = turnover / validOrderCount;
		}
		unitPrice = Double.parseDouble(String.format("%.2f", unitPrice));
		BusinessDataVO businessDataVO = BusinessDataVO.builder()
				.newUsers(newUsers)//新增用户数
				.orderCompletionRate(orderCompletionRate)//订单完成率
				.turnover(turnover)//营业额
				.unitPrice(unitPrice)//平均客单价
				.validOrderCount(validOrderCount)//有效订单数
				.build();
		return businessDataVO;
	}

	/**
	 * 导表
	 * @param begin
	 * @param end
	 * @return
	 */
	@Override
	public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
		Map map = new HashMap();
		map.put("begin", begin);
		map.put("end", end);
		// 1.新增用户数
		Integer newUsers = userMapper.countByMap(map);
		// 2.订单完成率
		Integer sumOrder = orderMapper.getCountOrderByStatus(map);//当天全部订单
		map.put("status", Orders.COMPLETED);
		Integer validOrderCount = orderMapper.getCountOrderByStatus(map);    // 5.有效订单数
		Double orderCompletionRate = 0.0;//订单完成率
		if (sumOrder != 0) {
			orderCompletionRate = validOrderCount.doubleValue() / sumOrder;
		}
		// 3.营业额
		Double turnover = orderMapper.sumByMap(map);
		turnover = turnover == null ? 0.0 : turnover;
		// 4.平均客单价
		Double unitPrice = 0.0;
		if (validOrderCount != 0) {
			unitPrice = turnover / validOrderCount;
		}
		unitPrice = Double.parseDouble(String.format("%.2f", unitPrice));
		BusinessDataVO businessDataVO = BusinessDataVO.builder()
				.newUsers(newUsers)//新增用户数
				.orderCompletionRate(orderCompletionRate)//订单完成率
				.turnover(turnover)//营业额
				.unitPrice(unitPrice)//平均客单价
				.validOrderCount(validOrderCount)//有效订单数
				.build();
		return businessDataVO;
	}

	/**
	 * 查询订单管理数据
	 *
	 * @return
	 */
	@Override
	public OrderOverViewVO overviewOrders() {
		// 获取今日的时间
		LocalDate dateTime = LocalDate.now();
		LocalDateTime begin = LocalDateTime.of(dateTime, LocalTime.MIN);
		LocalDateTime end = LocalDateTime.of(dateTime, LocalTime.MAX);
		Map map = new HashMap();
		map.put("begin", begin);
		map.put("end", end);

		// 根据状态，分别查询出待接单、待派送、派送中的订单数量
		map.put("status", Orders.TO_BE_CONFIRMED);
		Integer waitingOrders = orderMapper.getCountOrderByStatus(map);//待接单
		map.put("status", Orders.CONFIRMED);
		Integer deliveredOrders = orderMapper.getCountOrderByStatus(map);//待派送数量
		map.put("status", Orders.COMPLETED);
		Integer completedOrders = orderMapper.getCountOrderByStatus(map);//已完成数量
		map.put("status", Orders.CANCELLED);
		Integer cancelledOrders = orderMapper.getCountOrderByStatus(map);//取消
		map.put("status", null);
		Integer allOrders = orderMapper.getCountOrderByStatus(map);//全部订单


		// 将查询出的数据封装到orderStatisticsVO中响应
		OrderOverViewVO orderOverViewVO = new OrderOverViewVO();
		orderOverViewVO.setWaitingOrders(waitingOrders);//待接单数量
		orderOverViewVO.setDeliveredOrders(deliveredOrders);//待派送数量
		orderOverViewVO.setCompletedOrders(completedOrders);//已完成数量
		orderOverViewVO.setCancelledOrders(cancelledOrders);//已取消数量
		orderOverViewVO.setAllOrders(allOrders);//全部订单
		return orderOverViewVO;
	}

	/**
	 * 查询菜品总览
	 *
	 * @return
	 */
	@Override
	public DishOverViewVO getDishOverView() {
		Map map = new HashMap();
		map.put("status", StatusConstant.ENABLE);
		Integer sold = dishMapper.countByMap(map);

		map.put("status", StatusConstant.DISABLE);
		Integer discontinued = dishMapper.countByMap(map);

		return DishOverViewVO.builder()
				.sold(sold)
				.discontinued(discontinued)
				.build();
	}

	/**
	 * 查询套餐总览
	 *
	 * @return
	 */
	@Override
	public SetmealOverViewVO getSetmealOverView() {
		Map map = new HashMap();
		map.put("status", StatusConstant.ENABLE);
		Integer sold = setmealMapper.countByMap(map);

		map.put("status", StatusConstant.DISABLE);
		Integer discontinued = setmealMapper.countByMap(map);

		return SetmealOverViewVO.builder()
				.sold(sold)
				.discontinued(discontinued)
				.build();
	}

}
