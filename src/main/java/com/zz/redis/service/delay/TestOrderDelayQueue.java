package com.zz.redis.service.delay;

import com.zz.redis.rediscluster.redis.delayqueue.AbstractDelayQueueMachineFactory;
import com.zz.redis.service.TestOrderDelayQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 测试订单延时队列
 * User: zhouzhou
 * Date: 2019-09-26
 * Time: 15:14
 */
@Component
public class TestOrderDelayQueue extends AbstractDelayQueueMachineFactory {

    @Autowired
    private TestOrderDelayQueueService testOrderDelayQueueService;

    @Override
    public void invoke(String jobId) {
        testOrderDelayQueueService.cancelOrder(jobId);
    }

    @Override
    public String setDelayQueueName() {
        return "TestOrder";
    }


}
