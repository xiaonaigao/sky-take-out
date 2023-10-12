package com.sky.controller.user;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 下单，微信支付
 */
@RequestMapping("/user/order")
@RestController
@Slf4j
@Api(tags = "用户下单")
public class OrderController {
	@Autowired
	private OrderService orderService;

	/**
	 * 用户下单
	 * @param ordersSubmitDTO
	 * @return
	 */
	@PostMapping("/submit")
	@ApiOperation("用户下单")
	public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
		log.info("下单的信息{}",ordersSubmitDTO);
		//调用service
		OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
		return Result.success(orderSubmitVO);
	}

	/**
	 * 订单支付
	 *
	 * @param ordersPaymentDTO
	 * @return
	 */
	@PutMapping("/payment")
	@ApiOperation("订单支付")
	public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
		log.info("订单支付：{}", ordersPaymentDTO);
		OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
		log.info("生成预支付交易单：{}", orderPaymentVO);
		return Result.success(orderPaymentVO);
	}

	/**
	 * 历史订单
	 * @param ordersPageQueryDTO
	 * @return
	 */
	@GetMapping("/historyOrders")
	@ApiOperation("历史订单")
	public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
		// 分页查询的返回结果是PageResult(总记录数，当前的结果的集合)
		log.info("历史订单参数{}",ordersPageQueryDTO);
		PageResult page = orderService.historyOrders(ordersPageQueryDTO);
		return Result.success(page);
	}

	/**
	 * 查询订单详情
	 */
	@GetMapping("/orderDetail/{id}")
	@ApiOperation("查询订单详情")
	public Result<OrderVO> orderDetail(@PathVariable Long id){
		log.info("查询的id是{}",id);
		OrderVO orderVO = orderService.orderDetail(id);
		return Result.success(orderVO);
	}

	/**
	 * 订单取消
	 */
	@PutMapping("/cancel/{id}")
	@ApiOperation("取消订单")
	public Result cancelOrder(@PathVariable Long id){
		log.info("取消的id是{}",id);
		orderService.cancel(id);
		return Result.success();
	}

	/**
	 * 再来一单
	 */
	@PostMapping("/repetition/{id}")
	@ApiOperation("再来一单")
	public Result repetition(@PathVariable Long id){
		log.info("再来一单的id是{}",id);
		orderService.repetition(id);
		return Result.success();
	}
	/**
	 * 催单
	 */
	@GetMapping("/reminder/{id}")
	@ApiOperation("催单")
	public Result reminder(@PathVariable Long id){
		log.info("催单{}",id);
		orderService.reminder(id);
		return Result.success();
	}

}
