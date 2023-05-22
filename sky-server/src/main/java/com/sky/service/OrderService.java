package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.OrderDetail;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) throws Exception;

    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);


    OrderVO orderDetail(Long id);

    void cancel(Long id);

    void repetition(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO details(Long id);

    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    OrderStatisticsVO statistics();

    void payment(OrdersPaymentDTO ordersPaymentDTO);

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void delivery(Long id);

    void complete(Long id);

    void reminder(Long id);
}
