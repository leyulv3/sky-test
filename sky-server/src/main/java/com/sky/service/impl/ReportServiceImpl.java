package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        //reportMapper.getTurnover(begin,end);
        List<LocalDate> dates = new ArrayList<>();
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dates.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        dates.forEach(date -> {
            Map map = new HashMap<>();
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            map.put("begin", startTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });
        //数据封装
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> dates = new ArrayList<>();
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dates.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>(); //新增用户数
        List<Integer> totalUserList = new ArrayList<>(); //总用户数
        dates.forEach(date -> {
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            int userCount = userMapper.selectCountUser();
            int userNewCount = userMapper.selectCountNewUser(startTime, endTime);
            newUserList.add(userNewCount);
            totalUserList.add(userCount);
        });
        return UserReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dates.add(begin);
        }
        //每日订单数
        List orderCountList = new ArrayList();
        //每日有效订单数
        List validOrderCountList = new ArrayList();
        dates.forEach(date -> {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            orderCountList.add(orderMapper.getTotal(map));
            map.put("status", Orders.COMPLETED);
            orderMapper.getTotal(map);
            validOrderCountList.add(orderMapper.getTotal(map));
        });
        //订单总数
        HashMap<Object, Object> map = new HashMap<>();
        int totalOrderCount = orderMapper.getTotal(map);
        map.put("status", Orders.COMPLETED);
        Integer validOrderCount = orderMapper.getTotal(map);
        OrderReportVO build = OrderReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate((double) validOrderCount / totalOrderCount)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(orderCountList, ","))
                .build();
        StringUtils.join(orderCountList, ",");
        return build;
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {

        //商品名称列表，以逗号分隔，例如：鱼香肉丝,宫保鸡丁,水煮鱼
        //    private String nameList;
        //    //销量列表，以逗号分隔，例如：260,215,200
        //    private String numberList;
        List<LocalDate> dates = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("startTime", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("endTime", LocalDateTime.of(end, LocalTime.MAX));
        map.put("status", Orders.COMPLETED);
        List<GoodsSalesDTO> orders = orderMapper.selectMap(map);
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(orders.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ","))
                .numberList(StringUtils.join(orders.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ","))
                .build();
    }
}
