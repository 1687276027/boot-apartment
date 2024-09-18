package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.CodeUtil;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.app.mapper.UserInfoMapper;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.SmsService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserInfoMapper userInfoMapper;


    @Override
    public void getCode(String phone) {
        String code = CodeUtil.getRandomCode(6); // 生成验证码
        String key = RedisConstant.APP_LOGIN_PREFIX + phone; // 验证码key

        // 判断是否在60s内发送过验证码
        Boolean exciteKey = stringRedisTemplate.hasKey(key);
        if (exciteKey) {
            Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);// 获取过期时间
            if (expire != null && expire > 0) {
                throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
            }
        }

        smsService.sendCode(phone, code); // 发送验证码
        stringRedisTemplate.opsForValue().set(key, code, RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS); // 60s有效期
    }

    @Override
    public String login(LoginVo loginVo) {

        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        // 判断手机号和验证码是否为空
        if (phone == null) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
        }
        if (code == null) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }

        // 判断验证码是否在60s内发送过
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;
        String redisCode = stringRedisTemplate.opsForValue().get(key);
        if (redisCode == null) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);
        }

        // 判断验证码是否正确
        if (!redisCode.equals(code)) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
        }

        // 判断用户是否存在
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone, phone);
        UserInfo user = userInfoMapper.selectOne(queryWrapper);
        if (user == null) {
            // 创建新用户
            user = new UserInfo();
            user.setPhone(phone);
            user.setStatus(BaseStatus.ENABLE);
            user.setNickname("用户：" + phone.substring(7));
            userInfoMapper.insert(user);
        }

        // 判断用户是否被禁用
        if (user.getStatus() == BaseStatus.DISABLE) {
            throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }

        return JwtUtil.createToken(user.getId(), loginVo.getPhone());

    }

    @Override
    public UserInfoVo info(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        UserInfoVo userInfoVo = new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());
        return userInfoVo;
    }
}
