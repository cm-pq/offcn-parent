package com.offcn;

import org.aspectj.lang.annotation.AfterThrowing;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserStartApplication.class})
public class RedisTest {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    public  void  contextLoads(){
        redisTemplate.opsForValue().set("msg","欢迎你");
    }

}
