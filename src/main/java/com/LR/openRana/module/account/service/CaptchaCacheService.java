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

    /**
     * 验证验证码是否正确
     *
     * @param phone 手机号码
     * @param code  用户输入的验证码
     * @return 验证码正确返回true，否则返回false
     */
    public boolean validateCaptcha(String phone, String code) {
        // 从缓存中获取指定手机号对应的验证码
        String cacheCode = this.getCaptchaForPhone(phone);
        // 验证码使用后，从缓存中删除
        delete(phone);
        // 检查缓存中是否存有该手机号的验证码
        if (cacheCode == null) {
            return false;
        }
        // 比较用户输入的验证码和缓存中的验证码是否一致
        return cacheCode.equals(code);
    }

    /**
     * 检查指定手机号的验证码是否已存在
     *
     * @param phone 手机号码
     * @return 验证码已存在返回true，否则返回false
     */
    public boolean isExist(String phone) {
        // 利用utils工具类检查Redis中是否存在指定手机号的验证码
        return utils.hasKey(RedisPrefixConstants.PHONE_CAPTCHA_PREFIX + phone);
    }
}
