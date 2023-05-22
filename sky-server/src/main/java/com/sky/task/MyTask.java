package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.webscoket.WebSocketServer;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * 自定义定时任务类
 */
@Component
@Slf4j
public class MyTask {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 通过WebSocket每隔5秒向客户端发送消息
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendMessageToClient() {
        webSocketServer.sendToAllClient("这是来自服务端的消息：" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
    }

    /**
     * 处理支付超时订单
     */
    @Scheduled(cron = "0 0/20 * * * ?")
    public void processTimeoutOrder() {
        log.info("处理支付超时订单：{}", new Date());
        //1、查询超时的订单
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> orders = orderMapper.selectOverOrder(time, Orders.PENDING_PAYMENT);
        //2、将超时订单设置为取消
        if (orders != null && orders.size() > 0) {
            orders.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("支付超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.updateById(order);
            });
        }
    }

    /**
     * 处理“派送中”状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("处理派送中订单：{}", new Date());
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        List<Orders> orders = orderMapper.selectOverOrder(time, Orders.DELIVERY_IN_PROGRESS);
        if (!Collections.isEmpty(orders)) {
            orders.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                orderMapper.updateById(order);
            });
        }
    }
}