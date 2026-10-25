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

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    @Select("select * from orders where pickup_code = #{pickupCode}")
    Orders getByPickupCode(String pickupCode);

    void update(Orders orders);

    int updateStatusIfMatch(Orders orders, Integer expectedStatus);

    @Update("update orders set slot_released = 1 where id = #{id} and slot_released = 0")
    int markSlotReleased(Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO dto);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime} limit #{limit}")
    List<Orders> getByStatusAudOrderTimeLT(Integer status, LocalDateTime orderTime, Integer limit);

    Double sumByMap(Map map);

    Integer countByMap(Map map);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
