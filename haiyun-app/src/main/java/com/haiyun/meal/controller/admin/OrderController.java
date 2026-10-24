package com.haiyun.meal.controller.admin;

import com.haiyun.meal.annotation.RequirePermission;
import com.haiyun.meal.dto.OrdersCancelDTO;
import com.haiyun.meal.dto.OrdersConfirmDTO;
import com.haiyun.meal.dto.OrdersPageQueryDTO;
import com.haiyun.meal.dto.OrdersRejectionDTO;
import com.haiyun.meal.result.PageResult;
import com.haiyun.meal.result.Result;
import com.haiyun.meal.service.OrderService;
import com.haiyun.meal.vo.OrderStatisticsVO;
import com.haiyun.meal.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 订单管理接口
@RestController("adminOrderController")
@RequestMapping("/api/admin/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @RequirePermission("order:query")
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("条件搜索订单：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @RequirePermission("order:statistics")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics(){
        log.info("订单统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    @RequirePermission("order:query")
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable("id") Long id){
        log.info("订单详情，订单id为{}", id);
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     * @param orderConfirmDTO
     * @return
     */
    @RequirePermission("order:handle")
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO orderConfirmDTO){
        log.info("接单,{}", orderConfirmDTO);
        orderService.confirm(orderConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @RequirePermission("order:handle")
    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("拒单{}", ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @RequirePermission("order:cancel")
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        log.info("取消订单{}", ordersCancelDTO);
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     */
    @RequirePermission("order:handle")
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable("id") Long id){
        log.info("派送订单");
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     */
    @RequirePermission("order:handle")
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable("id") Long id){
        log.info("完成订单");
        orderService.complete(id);
        return Result.success();
    }
}
