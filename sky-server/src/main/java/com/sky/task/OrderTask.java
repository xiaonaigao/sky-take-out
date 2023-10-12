package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 定时任务类
 */
@Component
@Slf4j
public class OrderTask {
	@Autowired
	private OrderMapper orderMapper;

	/**
	 * 订单超时管理：15分钟自动取消
	 * 每分钟查询一次
	 * List<order>select * from order where status = 未支付1 and ordertime < 当前时间-15
	 * orderList查询出来的，循环取出一个一个order，把status取消6，cancel_time，cancel_reason
	 */
	@Scheduled(cron = "0 * * * * ?")
	public void processTimeoutOrder() {
		log.info("处理支付超时订单：{}", new Date());
		// 封装参数
		Integer status = Orders.PENDING_PAYMENT;//未支付
		LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-15);//超时15分钟
		// 查询出超时的订单
		List<Orders> ordersList = orderMapper.getTimeoutOrder(status, localDateTime);
		// 查询出的超时订单
		if (ordersList != null && ordersList.size() > 0) {
			for (Orders orders : ordersList) {
				orders.setStatus(Orders.CANCELLED);//订单状态为取消
				orders.setCancelTime(LocalDateTime.now());
				orders.setCancelReason("支付超时，自动取消");
				orderMapper.update(orders);//订单更新
			}
		}
	}


	/**
	 * 派送的订单：
	 * 每天凌晨1点，忘记点完成，自动完成，
	 */

	@Scheduled(cron = "0 * * * * ?")
	public void processDeliveryOrder() {
		log.info("处理派送中订单：{}", new Date());
		// 封装参数
		Integer status = Orders.DELIVERY_IN_PROGRESS;//派送中
		LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-60);//派送大于60分钟
		// 查询出超时的订单
		List<Orders> ordersList = orderMapper.getTimeoutOrder(status, localDateTime);
		// 查询出的超时订单
		if (ordersList != null && ordersList.size() > 0) {
			for (Orders orders : ordersList) {
				orders.setStatus(Orders.COMPLETED);//订单状态为取消
				orders.setDeliveryTime(LocalDateTime.now());
				orderMapper.update(orders);//订单更新
			}
		}
	}


}
