package com.haiyun.meal.service;

import com.haiyun.meal.vo.BusinessDataVO;
import com.haiyun.meal.vo.DishOverViewVO;
import com.haiyun.meal.vo.OrderOverViewVO;
import com.haiyun.meal.vo.SetmealOverViewVO;
import java.time.LocalDateTime;

/**
 * 工作台业务接口。
 */
public interface WorkspaceService {

    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询菜品总览
     * @return
     */
    DishOverViewVO getDishOverView();

    /**
     * 查询套餐总览
     * @return
     */
    SetmealOverViewVO getSetmealOverView();

}
