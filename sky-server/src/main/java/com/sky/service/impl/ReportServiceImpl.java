package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
	@Autowired
	private WorkspaceService workspaceService;

	/**
	 * 营业额统计接口
	 */
	@Override
	public TurnoverReportVO turnoverStatistics(LocalDate beginTime, LocalDate endTime) {
		// 1.返回的类型
		TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
		// 2.返回的日期
		List<LocalDate> localDateList = new ArrayList();
		while (!beginTime.equals(endTime)) {
			localDateList.add(beginTime);
			beginTime = beginTime.plusDays(+1);
		}
		localDateList.add(beginTime);
		turnoverReportVO.setDateList(StringUtils.join(localDateList, ","));
		// 3.返回金额
		List<Double> turnoverList = new ArrayList();
		for (LocalDate date : localDateList) {
			LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);//00:00
			LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);//23:59
			Map map = new HashMap();
			map.put("status", Orders.COMPLETED);
			map.put("begin", begin);
			map.put("end", end);
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
	public UserReportVO userStatistics(LocalDate beginTime, LocalDate endTime) {
		// 1.返回的类型
		UserReportVO userReportVO = new UserReportVO();
		// 2.返回的日期
		List<LocalDate> localDateList = new ArrayList();
		while (!beginTime.equals(endTime)) {
			localDateList.add(beginTime);
			beginTime = beginTime.plusDays(+1);
		}
		localDateList.add(beginTime);
		userReportVO.setDateList(StringUtils.join(localDateList, ","));
		// 3.统计用户
		List<Integer> newUserList = new ArrayList();//新增
		List<Integer> oldUserList = new ArrayList();//老用户
		for (LocalDate date : localDateList) {
			LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);//00:00
			LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);//23:59
			Map map = new HashMap();
			map.put("begin", begin);
			map.put("end", end);
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
	public OrderReportVO ordersStatistics(LocalDate beginTime, LocalDate endTime) {
		// 1.创建OrderReportVO
		OrderReportVO orderReportVO = new OrderReportVO();
		// dateList
		List<LocalDate> dateList = new ArrayList();
		while (!beginTime.equals(endTime)) {
			dateList.add(beginTime);
			beginTime = beginTime.plusDays(+1);
		}
		dateList.add(beginTime);
		orderReportVO.setDateList(StringUtils.join(dateList, ","));//1.日期列表

		// 2.订单数据
		List<String> orderCountList = new ArrayList();//每日订单数
		List<String> validOrderCountList = new ArrayList();//每日有效订单数
		Integer validOrderCount = 0; //5.有效订单数
		Integer totalOrderCount = 0;//订单总数
		for (LocalDate date : dateList) {
			Map map = new HashMap();
			LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);//当天最小的时间
			LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);//当天最大的时间
			map.put("begin", begin);
			map.put("end", end);
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
	public SalesTop10ReportVO top10(LocalDate beginTime, LocalDate endTime) {
		LocalDateTime begin = LocalDateTime.of(beginTime, LocalTime.MIN);//最小的时间
		LocalDateTime end = LocalDateTime.of(endTime, LocalTime.MAX);//最大的时间
		Map map = new HashMap();
		map.put("begin", begin);
		map.put("end", end);
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
	/**
	 * 导表
	 */
	@Override
	public void export(HttpServletResponse response) {
		//获取一个月的时间
		LocalDate begin  = LocalDate.now().minusDays(30);//开始
		LocalDate end  = LocalDate.now().minusDays(1);//结束
		//查询数据
		BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin,LocalTime.MIN),LocalDateTime.of(end,LocalTime.MAX));
		//获取excel
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
		try {
			// 创建excel模板表格对象
			XSSFWorkbook excel = new XSSFWorkbook(inputStream);
			// 获取excel的第一个sheet页
			XSSFSheet sheet = excel.getSheet("Sheet1");
			// 1行1列设置表头
			sheet.getRow(1).getCell(1).setCellValue(begin+"至"+end);
			// 获取第4行，进行填充今日数据
			XSSFRow row = sheet.getRow(3);
			row.getCell(2).setCellValue(businessData.getTurnover());//金额
			row.getCell(4).setCellValue(businessData.getOrderCompletionRate());//订单完成率
			row.getCell(6).setCellValue(businessData.getNewUsers());//新用户
			// 获取第5行，进行数据填充
			row = sheet.getRow(4);
			row.getCell(2).setCellValue(businessData.getValidOrderCount());//	有效订单
			row.getCell(4).setCellValue(businessData.getUnitPrice());//平均单价
			// 30天的详细数据
			for (int i = 0; i < 30; i++) {
				LocalDate date = begin.plusDays(i);//日期递增
				//每一天的详细数据
				businessData = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN),LocalDateTime.of(date,LocalTime.MAX));
				row = sheet.getRow(7 + i);//每一次循环完进行下一行
				//每一行的数据填充
				row.getCell(1).setCellValue(date.toString());
				row.getCell(2).setCellValue(businessData.getTurnover());
				row.getCell(3).setCellValue(businessData.getValidOrderCount());
				row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
				row.getCell(5).setCellValue(businessData.getUnitPrice());
				row.getCell(6).setCellValue(businessData.getNewUsers());
			}

			// 输出流到浏览器
			ServletOutputStream outputStream = response.getOutputStream();
			excel.write(outputStream);//写入到excel
			//关闭
			outputStream.flush();
			outputStream.close();
			excel.close();


		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
