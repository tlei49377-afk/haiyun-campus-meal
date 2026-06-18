package com.sky.service.impl;

import com.sky.entity.Orders;
import org.apache.commons.lang3.StringUtils;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        log.info("营业额数据统计：{}到{}", begin, end);

        // 创建日期集合,存放从begin到end的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            // 日期计算：begin加1天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 存放每日营业额数据
        List<Double> turnoverList = new ArrayList<>();

        // 遍历dateList
        for(LocalDate date:dateList){
            // 查询date日期对应的营业额数据，营业额是指：状态为“已完成”的订单金额总计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnoverList.add(turnover == null ? 0.0 : turnover);
        }

        // 创建日期对应的营业额集合和营业额数据集合
        String data = StringUtils.join(dateList, ",");
        String turnoverListtoString = StringUtils.join(turnoverList, ",");
        // 封装返回结果
        return TurnoverReportVO.builder()
                .dateList(data)
                .turnoverList(turnoverListtoString)
                .build();
    }
}
