package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
 * 用户下单
 */
public interface OrderService {

	/**
	 * 用户下单
	 * @param ordersSubmitDTO
	 * @return
	 */
	OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

	/**
	 * 支付
	 * @param ordersPaymentDTO
	 * @return
	 */
	OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;
	/**
	 * 历史订单
	 * @param ordersPageQueryDTO
	 * @return
	 */
	PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);
	/**
	 * 查询订单详情
	 */
	OrderVO orderDetail(Long id);
	/**
	 * 订单取消
	 */
	void cancel(Long id);
	/**
	 * 再来一单
	 */
	void repetition(Long id);

	/**
	 * 订单管理1：订单搜索
	 * @param ordersPageQueryDTO
	 * @return
	 */
	PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
	/**
	 * 查看订单的数量
	 */
	OrderStatisticsVO orderStatistics();
	/**
	 * 接单
	 */
	void confirm(OrdersConfirmDTO ordersConfirmDTO);
	/**
	 * 拒单
	 */
	void rejection(OrdersRejectionDTO ordersRejectionDTO);
	/**
	 * 取消订单
	 */
	void shopCancel(OrdersCancelDTO ordersCancelDTO);
	/**
	 * 派送订单
	 */
	void delivery(Long id);
	/**
	 * 完成订单
	 */
	void complete(Long id);
	/**
	 * 催单
	 */
	void reminder(Long id);
}
