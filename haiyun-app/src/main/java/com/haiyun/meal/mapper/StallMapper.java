package com.haiyun.meal.mapper;

import com.haiyun.meal.entity.Stall;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 档口数据访问接口。
 */
@Mapper
public interface StallMapper {
    /**
     * 按食堂查询档口列表。
     */
    @Select("select * from stall where canteen_id = #{canteenId} and (#{status} is null or status = #{status}) order by sort asc, id asc")
    List<Stall> listByCanteenId(Long canteenId, Integer status);

    /**
     * 查询档口列表。
     */
    @Select("select * from stall where (#{status} is null or status = #{status}) order by canteen_id asc, sort asc, id asc")
    List<Stall> list(Integer status);

    /**
     * 根据主键查询档口。
     */
    @Select("select * from stall where id = #{id}")
    Stall getById(Long id);

    /**
     * 新增档口。
     */
    @Insert("insert into stall(canteen_id,name,description,location,status,sort,create_time,update_time) " +
            "values(#{canteenId},#{name},#{description},#{location},#{status},#{sort},now(),now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Stall stall);

    /**
     * 修改档口。
     */
    void update(Stall stall);
}
