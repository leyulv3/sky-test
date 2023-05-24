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

    /**
     * 获取营业额数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 日期 营业额
     */
    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        //获取日期列表
        List<LocalDate> dates = getLocalDates(begin, end);
        //获取营业额列表
        List<Double> turnoverList = new ArrayList<>();
        //for循环遍历获取数据
        dates.forEach(date -> {
            //构建查询条件 当天时间和订单状态
            Map map = new HashMap<>();
            map.put("begin", date.atStartOfDay());
            map.put("end", date.atTime(LocalTime.MAX));
            map.put("status", Orders.COMPLETED);
            //查询当天的营业额
            Double turnover = orderMapper.sumByMap(map);
            //把查询到的数据插入到list中
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });
        //数据封装
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 获取用户数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 用户总量 新增用户
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //获取日期列表
        List<LocalDate> dates = getLocalDates(begin, end);
        List<Integer> newUserList = new ArrayList<>(); //新增用户数
        List<Integer> totalUserList = new ArrayList<>(); //总用户数
        //for循环遍历获取数据
        dates.forEach(date -> {
            //当天时间
            LocalDateTime startTime = date.atStartOfDay();
            LocalDateTime endTime = date.atTime(LocalTime.MAX);
            //查询当天的用户总数和新用户数
            int userCount = userMapper.selectCountUser();
            int userNewCount = userMapper.selectCountNewUser(startTime, endTime);
            //把查询到的数据插入到list中
            newUserList.add(userNewCount);
            totalUserList.add(userCount);
        });
        //返回数据
        return UserReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 获取订单数据
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 日期，每日订单数 每日有效订单数 总订单数 有效订单数 订单完成率
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = getLocalDates(begin, end);
        //每日订单数
        List orderCountList = new ArrayList();
        //每日有效订单数
        List validOrderCountList = new ArrayList();
        dates.forEach(date -> {
            //构建查询条件
            HashMap<Object, Object> map = new HashMap<>();
            map.put("begin", date.atStartOfDay());
            map.put("end", date.atTime(LocalTime.MAX));
            //订单总数
            orderCountList.add(orderMapper.getTotal(map));
            map.put("status", Orders.COMPLETED);
            //有效订单数
            orderMapper.getTotal(map);
            validOrderCountList.add(orderMapper.getTotal(map));
        });
        HashMap<Object, Object> map = new HashMap<>();
        //总订单数
        int totalOrderCount = orderMapper.getTotal(map);
        map.put("status", Orders.COMPLETED);
        //总有效订单数
        Integer validOrderCount = orderMapper.getTotal(map);
        //数据封装
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate((double) validOrderCount / totalOrderCount)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(orderCountList, ","))
                .build();
    }

    /**
     * 销量前十商品
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 销量前十商品
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        //构建查询条件
        Map<String, Object> map = new HashMap<>();
        map.put("startTime", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("endTime", LocalDateTime.of(end, LocalTime.MAX));
        map.put("status", Orders.COMPLETED);
        //查询销量前十的商品
        List<GoodsSalesDTO> orders = orderMapper.selectMap(map);
        //数据封装
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(orders.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ","))
                .numberList(StringUtils.join(orders.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ","))
                .build();
    }

    /**
     * 获取日期列表
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 日期列表
     */
    private static List<LocalDate> getLocalDates(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dates.add(begin);
        }
        return dates;
    }
}
