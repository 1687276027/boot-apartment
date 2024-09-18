package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.web.app.mapper.UserInfoMapper;
import com.atguigu.lease.web.app.service.SmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmsServiceImplTest {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private SmsService smsService;

    @Test
    void sendSms() {
        smsService.sendCode("15767724268", "1234");
    }

    @Test
    void testJdbc() {
        UserInfo userInfo = userInfoMapper.selectById(1);
    }
}