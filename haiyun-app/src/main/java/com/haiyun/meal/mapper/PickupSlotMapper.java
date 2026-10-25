package com.haiyun.meal.mapper;

import com.haiyun.meal.dto.PickupSlotQueryDTO;
import com.haiyun.meal.entity.PickupSlot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 取餐时段数据访问接口。
 */
@Mapper
public interface PickupSlotMapper {
    /**
     * 新增取餐时段。
     */
    @Insert("insert into pickup_slot(stall_id,slot_date,start_time,end_time,capacity,booked,status,version,create_time,update_time) " +
            "values(#{stallId},#{slotDate},#{startTime},#{endTime},#{capacity},0,1,0,now(),now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PickupSlot slot);

    /**
     * 按条件查询取餐时段。
     */
    List<PickupSlot> list(PickupSlotQueryDTO query);

    /**
     * 根据主键查询取餐时段。
     */
    @Select("select * from pickup_slot where id = #{id}")
    PickupSlot getById(Long id);

    /**
     * 按档口、日期和开始时间查询时段。
     */
    @Select("select * from pickup_slot where stall_id = #{stallId} and slot_date = #{slotDate} and start_time = #{startTime} limit 1")
    PickupSlot getByUniqueKey(Long stallId, java.time.LocalDate slotDate, java.time.LocalTime startTime);

    /** 原子占位：判断和加一在同一条 SQL 中完成。 */
    @Update("update pickup_slot set booked = booked + 1, version = version + 1, update_time = now() " +
            "where id = #{id} and status = 1 and booked < capacity")
    int occupy(Long id);

    /** 释放库存，booked 不会减到负数。 */
    @Update("update pickup_slot set booked = booked - 1, version = version + 1, update_time = now() " +
            "where id = #{id} and booked > 0")
    int release(Long id);

    /**
     * 修改取餐时段启停状态。
     */
    @Update("update pickup_slot set status = #{status}, update_time = now() where id = #{id}")
    int updateStatus(Long id, Integer status);

    /**
     * 修改取餐时段信息。
     */
    void update(PickupSlot slot);

    /**
     * 仅删除尚未被预约的取餐时段。
     */
    @Delete("delete from pickup_slot where id = #{id} and booked = 0")
    int deleteIfUnbooked(Long id);
}
