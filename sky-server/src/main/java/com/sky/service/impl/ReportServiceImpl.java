package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计接口
 */
@Service
public class ReportServiceImpl implements ReportService {
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private UserMapper userMapper;

	/**
	 * 营业额统计接口
	 */
	@Override
	public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
		// 1.返回的类型
		TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
		// 2.返回的日期
		List<LocalDate> localDateList = new ArrayList();
		while (!begin.equals(end)) {
			localDateList.add(begin);
			begin = begin.plusDays(+1);
		}
		localDateList.add(begin);
		turnoverReportVO.setDateList(StringUtils.join(localDateList, ","));
		// 3.返回金额
		List<Double> turnoverList = new ArrayList();
		for (LocalDate date : localDateList) {
			LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//00:00
			LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//23:59
			Map map = new HashMap();
			map.put("status", Orders.COMPLETED);
			map.put("begin", beginTime);
			map.put("end", endTime);
			Double turnover = orderMapper.sumByMap(map);
			turnover = turnover == null ? 0.0 : turnover;
			turnoverList.add(turnover);
		}
		turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ","));
		return turnoverReportVO;
	}

	/**
	 * 用户统计
	 */
	@Override
	public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
		// 1.返回的类型
		UserReportVO userReportVO = new UserReportVO();
		// 2.返回的日期
		List<LocalDate> localDateList = new ArrayList();
		while (!begin.equals(end)) {
			localDateList.add(begin);
			begin = begin.plusDays(+1);
		}
		localDateList.add(begin);
		userReportVO.setDateList(StringUtils.join(localDateList, ","));
		// 3.统计用户
		List<Integer> newUserList = new ArrayList();//新增
		List<Integer> oldUserList = new ArrayList();//老用户
		for (LocalDate date : localDateList) {
			LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//00:00
			LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//23:59
			Map map = new HashMap();
			map.put("begin", beginTime);
			map.put("end", endTime);
			Integer newUser = userMapper.countByMap(map);
			map.put("begin", null);
			Integer oldUser = userMapper.countByMap(map);
			newUserList.add(newUser);
			oldUserList.add(oldUser);
		}
		userReportVO.setNewUserList(StringUtils.join(newUserList, ","));
		userReportVO.setTotalUserList(StringUtils.join(oldUserList, ","));


		return userReportVO;
	}

	/**
	 * 订单统计
	 */
	@Override
	public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
		// 1.创建OrderReportVO
		OrderReportVO orderReportVO = new OrderReportVO();
		// dateList
		List<LocalDate> dateList = new ArrayList();
		while (!begin.equals(end)) {
			dateList.add(begin);
			begin = begin.plusDays(+1);
		}
		dateList.add(begin);
		orderReportVO.setDateList(StringUtils.join(dateList, ","));//1.日期列表

		// 2.订单数据
		List<String> orderCountList = new ArrayList();//每日订单数
		List<String> validOrderCountList = new ArrayList();//每日有效订单数
		Integer validOrderCount = 0; //5.有效订单数
		Integer totalOrderCount = 0;//订单总数
		for (LocalDate date : dateList) {
			Map map = new HashMap();
			LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//当天最小的时间
			LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//当天最大的时间
			map.put("beginTime", beginTime);
			map.put("endTime", endTime);
			Integer sumOrder = orderMapper.getCountOrderByStatus(map);//当天全部订单
			orderCountList.add(sumOrder + "");//2.每日订单数
			totalOrderCount += sumOrder;//4.订单总数
			map.put("status", Orders.COMPLETED);
			Integer completeOrder = orderMapper.getCountOrderByStatus(map);//当天完成的订单
			validOrderCountList.add(completeOrder + "");//3.每日有效订单数
			validOrderCount += completeOrder;  //5.有效订单数
		}
		Double orderCompletionRate = 0.0;//订单率
		if (totalOrderCount != 0) {
			orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
		}

		// 3.赋值
		orderReportVO.setOrderCountList(StringUtils.join(orderCountList, ","));//每日订单数
		orderReportVO.setValidOrderCountList(StringUtils.join(validOrderCountList, ","));//每日有效订单数
		orderReportVO.setTotalOrderCount(totalOrderCount);//订单总数
		orderReportVO.setValidOrderCount(validOrderCount);//5.有效订单数
		orderReportVO.setOrderCompletionRate(orderCompletionRate);//订单率
		return orderReportVO;
	}

	/**
	 * 查询销量排名top10
	 */
	@Override
	public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
		LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);//最小的时间
		LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);//最大的时间
		Map map = new HashMap();
		map.put("beginTime", beginTime);
		map.put("endTime", endTime);
		List<GoodsSalesDTO> goodsSalesList = orderMapper.getTopDish(map);
		List<String>nameList = new ArrayList();//名称
		List<String>numberList = new ArrayList();//数量
		for (GoodsSalesDTO goodsSales : goodsSalesList) {
			nameList.add(goodsSales.getName());
			numberList.add(goodsSales.getNumber()+"");
		}

		//封装参数
		SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
				.nameList(StringUtils.join(nameList, ","))
				.numberList(StringUtils.join(numberList, ","))
				.build();
		return salesTop10ReportVO;
	}
}
