package com.zz.redis.controller;

import com.zz.redis.config.RedisUtil;
import com.zz.redis.config.ZCachePut;
import com.zz.redis.config.ZCacheable;
import com.zz.redis.config.bean.Student;
import com.zz.redis.service.delay.TestOrderDelayQueue;
import com.zz.redis.service.delay.TestRegDelayQueue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Description:
 * User: zhouzhou
 * Date: 2019-06-03
 * Time: 17:47
 */
@RestController
@Api("缓存测试")
public class RedisTestController {

    private Logger logger = LoggerFactory.getLogger(RedisTestController.class);


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TestOrderDelayQueue testOrderDelayQueue;

    @Autowired
    private TestRegDelayQueue testRegDelayQueue;

    // ---------------------  字符串测试  ---------------------------
    @ApiOperation("redis测试")
    @RequestMapping(value = "/setValue/str", method = RequestMethod.POST)
    public String setStrValue(String key) {
        redisUtil.set(key, "zhouzhou" + new Random().nextInt(100));
        return "插入成功";
    }

    @ApiOperation("缓存查询")
    @RequestMapping(value = "/getValue/str", method = RequestMethod.GET)
    @ZCacheable(cacheName = "student", expireTime = 10, key = "#key")
    public String getStrValue(String key) {
        logger.info("获取中");
        return key + new Random().nextInt(100);
    }

    @ApiOperation("缓存更新")
    @RequestMapping(value = "/updateValue/str", method = RequestMethod.PATCH)
    @ZCachePut(cacheName = "student", expireTime = 10, key = "#key")
    public String putStrValue(String key) {
        logger.info("更新中");
        return key + new Random().nextInt(100);
    }

    // ---------------------  对象测试  ---------------------------
    @ApiOperation("缓存查询根据类")
    @RequestMapping(value = "/getValue/stu", method = RequestMethod.GET)
    @ZCacheable(cacheName = "student", expireTime = 10, key = "#student.name")
    public Student getStuValue(Student student) {
        logger.info("获取bean中的对象");
        Student studentResult = Student.builder().name(student.getName()).age(new Random().nextInt(100)).sex("男").build();
        return studentResult;
    }

    @ApiOperation("缓存更新根据类")
    @RequestMapping(value = "/updateValue/stu", method = RequestMethod.PATCH)
    @ZCachePut(cacheName = "student", expireTime = 10, key = "#student.name")
    public Student updateStuValue(Student student) {
        logger.info("获取bean中的对象");
        Student studentResult = Student.builder().name(student.getName()).age(new Random().nextInt(100)).sex("男").build();
        return studentResult;
    }

    // ------------------------  延时队列 -------------------
    @ApiOperation("添加定时orderId")
    @RequestMapping(value = "/addDelayOrder/{orderId}/{time}", method = RequestMethod.POST)
    public Object addZset(@PathVariable String orderId, @PathVariable Integer time) {

        boolean flag = testOrderDelayQueue.addJobId(orderId, time);
        return String.format("已经存入了订单id{%s},延时{%s}秒", orderId, time);

    }

    @ApiOperation("添加定时RegId")
    @RequestMapping(value = "/addDelayRegOrder/{orderId}/{time}", method = RequestMethod.POST)
    public Object addReg(@PathVariable String orderId, @PathVariable Integer time) {

        boolean flag = testRegDelayQueue.addJobId(orderId, time);
        return String.format("已经存入了订单id{%s},延时{%s}秒", orderId, time);

    }

}
