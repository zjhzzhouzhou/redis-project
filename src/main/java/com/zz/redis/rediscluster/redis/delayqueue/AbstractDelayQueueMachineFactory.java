package com.zz.redis.rediscluster.redis.delayqueue;

import com.zz.redis.rediscluster.redis.cluster.JedisClusterCache;
import com.zz.redis.utils.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Description: 延时队列工厂
 * User: zhouzhou
 * Date: 2019-09-26
 * Time: 14:32
 */
public abstract class AbstractDelayQueueMachineFactory {

    protected Logger logger = LoggerFactory.getLogger(AbstractDelayQueueMachineFactory.class);

    @Autowired
    protected JedisClusterCache jedisClusterCache;

    /**
     * 插入任务id
     *
     * @param jobId 任务id(队列内唯一)
     * @param time  延时时间(单位 :秒)
     * @return 是否插入成功
     */
    public boolean addJobId(String jobId, Integer time) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, time);
        long delaySeconds = instance.getTimeInMillis() / 1000;
        Long zadd = jedisClusterCache.zadd(setDelayQueueName(), delaySeconds, jobId);
        return zadd > 0;

    }

    private void startDelayQueueMachine() {
        logger.info(String.format("延时队列机器{%s}开始运作", setDelayQueueName()));

        // 发生异常捕获并且继续不能让战斗停下来
        while (true) {
            try {
                // 获取当前时间的时间戳
                long now = System.currentTimeMillis() / 1000;
                // 获取当前时间前的任务列表
                Set<Tuple> tuples = jedisClusterCache.zrangeByScoreWithScores(setDelayQueueName(), 0, now);
                // 如果不为空则遍历判断其是否满足取消要求
                if (!CollectionUtils.isEmpty(tuples)) {
                    for (Tuple tuple : tuples) {

                        String jobId = tuple.getElement();
                        Long num = jedisClusterCache.zrem(setDelayQueueName(), jobId);
                        // 如果移除成功, 则取消订单
                        if (num > 0) {
                            ThreadPoolUtil.execute(() -> invoke(jobId));
                        }
                    }
                }

            } catch (Exception e) {
                logger.warn(String.format("处理延时任务发生异常,异常原因为{%s}", e.getMessage()), e);
            } finally {
                // 间隔一秒钟搞一次
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    /**
     * 最终执行的任务方法
     *
     * @param jobId 任务id
     */
    public abstract void invoke(String jobId);


    /**
     * 要实现延时队列的名字
     */
    public abstract String setDelayQueueName();


    @PostConstruct
    public void init() {
        new Thread(this::startDelayQueueMachine).start();
    }

}
