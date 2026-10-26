package com.haiyun.meal.mapper;

import com.haiyun.meal.entity.Canteen;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 食堂数据访问接口。
 */
@Mapper
public interface CanteenMapper {
    /**
     * 查询食堂列表。
     */
    @Select("select * from canteen where (#{status} is null or status = #{status}) order by sort asc, id asc")
    List<Canteen> list(Integer status);

    /**
     * 根据主键查询食堂。
     */
    @Select("select * from canteen where id = #{id}")
    Canteen getById(Long id);

    /**
     * 新增食堂。
     */
    @Insert("insert into canteen(name,address,open_time,close_time,status,sort,create_time,update_time) " +
            "values(#{name},#{address},#{openTime},#{closeTime},#{status},#{sort},now(),now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Canteen canteen);

    /**
     * 修改食堂。
     */
    void update(Canteen canteen);
}
