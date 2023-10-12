package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import com.sky.vo.SalesTop10ReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author wang
 * @version 1.0
 */
@Mapper
public interface OrderMapper {
	/**
	 * 用户下单
	 *
	 * @param orders
	 */
	void save(Orders orders);

	/**
	 * 根据订单号查询订单
	 *
	 * @param outTradeNo
	 * @return
	 */
	@Select("select * from orders where number = #{outTradeNo}")
	Orders getByNumber(String outTradeNo);

	/**
	 * 更新订单状态
	 *
	 * @param orders
	 */
	void update(Orders orders);

	/**
	 * 分页历史订单
	 *
	 * @param ordersPageQueryDTO
	 * @return
	 */
	Page<OrderVO> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

	/**
	 * 分页条件查询并按下单时间排序
	 *
	 * @param ordersPageQueryDTO
	 */
	Page<Orders> pageQueryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

	/**
	 * 根据id查询订单
	 *
	 * @param id
	 * @return
	 */
	@Select("select * from orders where id = #{id}")
	Orders getById(Long id);

	/**
	 * 查看订单各状态的数量
	 */
	OrderStatisticsVO countByOrderStatis();

	/**
	 * 根据状态统计订单数量2
	 *
	 * @param status
	 */
	@Select("select count(id) from orders where status = #{status}")
	Integer countStatus(Integer status);

	/**
	 * 订单超时
	 *
	 * @param status
	 * @param localDateTime
	 * @return
	 */
	@Select("select * from orders where status = #{status} and order_time < #{localDateTime}")
	List<Orders> getTimeoutOrder(Integer status, LocalDateTime localDateTime);

	/**
	 * 营业额统计接口
	 * @param map
	 * @return
	 */
	Double sumByMap(Map map);

	/**
	 * 查询订单
	 * @param map
	 * @return
	 */
	Integer getCountOrderByStatus(Map map);

	/**
	 * 查询订单top10
	 */
	List<GoodsSalesDTO> getTopDish(Map map);


}
