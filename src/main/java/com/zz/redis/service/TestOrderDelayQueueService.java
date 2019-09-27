package com.zz.redis.service;

import org.springframework.stereotype.Service;

/**
 * Description:
 * User: zhouzhou
 * Date: 2019-09-26
 * Time: 15:21
 */
@Service
public class TestOrderDelayQueueService {


    public void cancelOrder(String orderNumber) {
        System.out.println(System.currentTimeMillis() + "ms:redis消费了一个任务：消费的订单OrderId为" + orderNumber);
    }

    public void cancelReg(String orderNumber) {
        System.out.println(System.currentTimeMillis() + "ms:redis消费了一个任务：消费的挂号订单OrderId为" + orderNumber);
    }
}
