package com.LR.openRana.module.account.service;


import com.LR.openRana.common.LLException;
import com.LR.openRana.constants.RedisPrefixConstants;
import com.LR.openRana.module.account.Captcha;
import com.LR.openRana.module.account.repository.RedisRepository;
import com.LR.openRana.utils.DataCheckerUtils;
import com.LR.openRana.utils.DateInitUtils;
import com.LR.openRana.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 验证码缓存服务类，负责验证码的生成、存储、获取和删除操作。
 */
@Service
public class CaptchaCacheService {

    private final RedisRepository utils; // Redis操作工具类

    @Autowired
    public CaptchaCacheService(RedisRepository utils) {
        this.utils = utils;
    }

    /**
     * 生成并插入验证码到缓存。
     *
     * @param phone 用户手机号
     * @return 生成的验证码对象，包含手机号和验证码代码
     */
    public Captcha insertCaptchaForPhone(String phone) {
        if (!DataCheckerUtils.isPhoneNumber(phone)) {
            throw new LLException("请输入正确手机号");
        }
        Captcha captcha = new Captcha();
        captcha.setPhone(phone);
        captcha.setCode(RandomUtils.generateNumberRandomString(4)); // 生成4位验证码
        if (DataCheckerUtils.isLR(phone)) {
            utils.set(RedisPrefixConstants.PHONE_CAPTCHA_PREFIX + captcha.getPhone(), captcha.getCode(), DateInitUtils.cacheDayToSecond(7));
        } else {
            utils.set(RedisPrefixConstants.PHONE_CAPTCHA_PREFIX + captcha.getPhone(), captcha.getCode(), DateInitUtils.cacheMinuteToSecond(5)); // 将验证码存储到Redis，设置过期时间
        }
        return captcha;
    }

    /**
     * 根据手机号从缓存中获取验证码。
     *
     * @param phone 用户手机号
     * @return 缓存中的验证码代码字符串。如果验证码不存在或已过期，抛出LLException异常。
     * @throws LLException 如果验证码不存在或已过期
     */
    public String getCaptchaForPhone(String phone) {
        if (!utils.hasKey(RedisPrefixConstants.PHONE_CAPTCHA_PREFIX + phone)) {
            throw new LLException("验证码不存在或已过期");
        }
        return utils.get(RedisPrefixConstants.PHONE_CAPTCHA_PREFIX + phone);
    }

    /**
     * 根据手机号删除缓存中的验证码。
     *
     * @param phone 用户手机号
     */
    public void delete(String phone) {
        utils.delete(RedisPrefixConstants.PHONE_CAPTCHA_PREFIX + phone); // 从Redis删除验证码
    }

    public boolean validateCaptcha(String phone, String code) {
        String cacheCode = this.getCaptchaForPhone(phone);
        delete(phone);
        if (cacheCode == null) {
            return false;
        }
        return cacheCode.equals(code);
    }

    public boolean isExist(String phone) {
        return utils.hasKey(RedisPrefixConstants.PHONE_CAPTCHA_PREFIX + phone);
    }
}
