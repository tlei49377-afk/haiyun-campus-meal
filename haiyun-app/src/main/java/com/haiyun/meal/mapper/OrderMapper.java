package com.haiyun.meal.mapper;

import com.github.pagehelper.Page;
import com.haiyun.meal.dto.GoodsSalesDTO;
import com.haiyun.meal.dto.OrdersPageQueryDTO;
import com.haiyun.meal.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问接口。
 */
@Mapper
public interface OrderMapper {
    /**
     * 新增预约订单。
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单。
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 根据取餐码查询订单。
     */
    @Select("select * from orders where pickup_code = #{pickupCode}")
    Orders getByPickupCode(String pickupCode);

    /**
     * 更新订单信息。
     */
    void update(Orders orders);

    /**
     * 按预期状态原子更新订单。
     */
    int updateStatusIfMatch(Orders orders, Integer expectedStatus);

    /**
     * 原子标记订单时段库存已释放。
     */
    @Update("update orders set slot_released = 1 where id = #{id} and slot_released = 0")
    int markSlotReleased(Long id);

    /**
     * 按条件分页查询订单。
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO dto);

    /**
     * 根据主键查询订单。
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 统计指定状态的订单数量。
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 分批查询指定状态且超时的订单。
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime} limit #{limit}")
    List<Orders> getByStatusAudOrderTimeLT(Integer status, LocalDateTime orderTime, Integer limit);

    /**
     * 按条件汇总订单金额。
     */
    Double sumByMap(Map map);

    /**
     * 按条件统计订单数量。
     */
    Integer countByMap(Map map);

    /**
     * 查询指定时间范围内的销量前十商品。
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
